package com.csd.blockneat.infrastructure.persistence;

import an.awesome.pipelinr.Voidy;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.ExceptionCode;
import com.csd.blockneat.application.accounts.commands.CreateAccountCommand;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.crypto.ECDSA;
import com.csd.blockneat.application.crypto.SHA512;
import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.ledger.LedgerRepository;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import com.csd.blockneat.application.users.commands.CreateUserCommand;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.StringOperators.RegexMatch.valueOf;

@Slf4j
@EqualsAndHashCode
@Repository
public class InMemoryLedger implements LedgerRepository, Serializable {

    private Queue<WriteCommand> commands;

    @Getter
    private transient final MongoTemplate mongoTemplate;

    public InMemoryLedger(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        commands = new ConcurrentLinkedQueue<>();
        mongoTemplate.insert(new ValidatedBlock(new Block(0, 0, "", new ArrayList<>()), "", 0), "validatedBlocks");
    }

    public CreateUserCommand getConfirmedUserCommand(String userId) {
        var filterCommand = filterByCommand("CreateUserCommand");

        var unwind = Aggregation.unwind("transactions");

        var projectUser = projectUser();

        var filterUser = Aggregation.match(Criteria.where("userId.email").is(userId));

        var user = Aggregation.newAggregation(filterCommand, unwind, projectUser, filterUser);

        return mongoTemplate.aggregate(user, "validatedBlocks", CreateUserCommand.class).getUniqueMappedResult();
    }

    public List<CreateUserCommand> getAllConfirmedUsers() {
        var filterCommand = filterByCommand("CreateUserCommand");

        var unwind = Aggregation.unwind("transactions");

        var projectUser = projectUser();

        var user = Aggregation.newAggregation(filterCommand, unwind, projectUser);

        return mongoTemplate.aggregate(user, "validatedBlocks", CreateUserCommand.class).getMappedResults();
    }

    private ProjectionOperation projectUser() {
        return Aggregation.project()
                .and("transactions.userId").as("userId")
                .and("transactions.signBase64").as("signBase64")
                .and("transactions._class").as("_class")
                .andExclude("_id");
    }

    private ProjectionOperation filterByCommand(String command) {
        return Aggregation.project()
                .and(filter("$block.transactions")
                        .as("transaction")
                        .by(valueOf("$$transaction._class").regex(command)))
                .as("transactions");
    }

    @Override
    public boolean append(WriteCommand command) {
        return commands.add(command);
    }

    public Stream<WriteCommand> getPendingCommandsStream() {
        return commands.stream();
    }

    @Override
    public Block getNextBlock() {
        List<WriteCommand> transactions = commands.stream()
                .limit(16)
                .collect(Collectors.toList());
        if (transactions.size() != 16) {
            return null;
        }
        Query id = new Query().limit(1)
                .with(Sort.by(Sort.Direction.DESC, "block.id"));
        var validatedBlock = mongoTemplate.findOne(id, ValidatedBlock.class, "validatedBlocks");
        assert validatedBlock != null;
        return new Block(validatedBlock.block().id() + 1, 0, validatedBlock.hash(), transactions);
    }

    @Override
    public boolean receiveBlock(ValidatedBlock validatedBlock) {
        Block minedBlock = validatedBlock.block();
        String validatedHash = SHA512.hexHash(minedBlock.toString());
        if (!validatedHash.startsWith("000") || !validatedBlock.hash().equals(validatedHash)) {
            return false;
        }
        var minedTransactions = new ArrayList<>(minedBlock.transactions());
        WriteCommand reward = minedTransactions.remove(0);
        Block nextBlock = getNextBlock();

        if (nextBlock.id() != minedBlock.id() ||
                !nextBlock.transactions().equals(minedTransactions) ||
                !nextBlock.previousBlockHash().equals(minedBlock.previousBlockHash()) ||
                !(reward instanceof LoadMoneyCommand payout) ||
                validate(payout).left() != ExceptionCode.SUCCESS) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            commands.remove();
        }
        mongoTemplate.insert(validatedBlock, "validatedBlocks");
        return true;
    }

    @Override
    public List<ValidatedBlock> getBlocks() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "block.id"));
        return mongoTemplate.find(query, ValidatedBlock.class, "validatedBlocks");
    }

    public Account getUnconfirmed(String accountId) {
        var accounts = commands.stream()
                .filter(CreateAccountCommand.class::isInstance)
                .map(CreateAccountCommand.class::cast)
                .filter(accountCommand -> accountCommand.accountId().equals(accountId))
                .map(accountCommand -> new Account(accountCommand.accountId(), accountCommand.userId()))
                .toList();
        if (!accounts.isEmpty()) {
            return accounts.get(0);
        } else {
            return getConfirmed(accountId);
        }
    }

    public Account getConfirmed(String accountId) {
        var filterCommand = filterByCommand("CreateAccountCommand");

        var unwind = Aggregation.unwind("transactions");

        var projectUser = projectAccount();

        var filterUser = Aggregation.match(Criteria.where("accountId").is(accountId));

        var account = Aggregation.newAggregation(filterCommand, unwind, projectUser, filterUser);

        var accountCommand = mongoTemplate.aggregate(account, "validatedBlocks", CreateAccountCommand.class).getUniqueMappedResult();
        return accountCommand != null ? new Account(accountCommand.accountId(), accountCommand.userId()) : null;
    }

    public List<Account> getAllConfirmedAccounts() {
        return getConfirmedCreateAccountCommands().stream()
                .map(accountCommand -> new Account(accountCommand.accountId(), accountCommand.userId()))
                .toList();
    }

    public List<CreateAccountCommand> getConfirmedCreateAccountCommands() {
        var filterCommand = filterByCommand("CreateAccountCommand");

        var unwind = Aggregation.unwind("transactions");

        var projectUser = projectAccount();

        var account = Aggregation.newAggregation(filterCommand, unwind, projectUser);

        return mongoTemplate.aggregate(account, "validatedBlocks", CreateAccountCommand.class)
                .getMappedResults();
    }

    public List<WriteCommand> getAllConfirmedTransactions() {
        var filterCommand = Aggregation.project()
                .and(filter("$block.transactions")
                        .as("transaction")
                        .by(valueOf("$$transaction._class")
                                .regex("LoadMoneyCommand|SendTransactionCommand")))
                .as("transactions");

        var unwind = Aggregation.unwind("transactions");

        var projectUser = Aggregation.project()
                .and("transactions.accountId").as("accountId")
                .and("transactions.from").as("from")
                .and("transactions.to").as("to")
                .and("transactions.value").as("value")
                .and("transactions.signBase64").as("signBase64")
                .and("transactions._class").as("_class")
                .andExclude("_id");

        var account = Aggregation.newAggregation(filterCommand, unwind, projectUser);

        return mongoTemplate.aggregate(account, "validatedBlocks", WriteCommand.class).getMappedResults();
    }

    private ProjectionOperation projectAccount() {
        return Aggregation.project()
                .and("transactions.userId").as("userId")
                .and("transactions.signBase64").as("signBase64")
                .and("transactions.accountId").as("accountId")
                .and("transactions._class").as("_class")
                .andExclude("_id");
    }

    private Either<Voidy> validate(LoadMoneyCommand command) {
        Account account = getUnconfirmed(command.accountId());
        if (account == null)
            return Either.failure(ExceptionCode.ACCOUNT_DOES_NOT_EXIST);
        if (!ECDSA.verifySign(account.userId().base64pk(), command.signBase64(), command.accountId() + command.value()))
            return Either.failure(ExceptionCode.INVALID_SIGNATURE);
        else if (command.value() < 0)
            return Either.failure(ExceptionCode.INVALID_VALUE);

        return Either.success();
    }

    public byte[] getSnapshot() {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(this); //TODO blocks
            return byteOut.toByteArray();
        } catch (IOException e) {
            log.warn("Error while taking snapshot", e);
        }
        return new byte[0];
    }

    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(state);
             ObjectInput objIn = new ObjectInputStream(byteIn)) {
            var replicaLedger = (InMemoryLedger) objIn.readObject();
            commands = replicaLedger.commands; //TODO blocks
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while installing snapshot", e);
        }
    }
}
