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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@EqualsAndHashCode
@Repository
public class InMemoryLedger implements LedgerRepository, Serializable {

    private Queue<WriteCommand> commands;
    @Getter
    private List<ValidatedBlock> blocks;

    public InMemoryLedger() {
        commands = new ConcurrentLinkedQueue<>();
        blocks = new ArrayList<>();
        blocks.add(new ValidatedBlock(new Block(0, 0, "", new ArrayList<>()), ""));
    }

    @Override
    public boolean append(WriteCommand command) {
        return commands.add(command);
    }

    @Override
    public Stream<WriteCommand> getUnconfirmedCommandsStream() {
        return Stream.concat(getConfirmedCommandsStream(), commands.stream());
    }

    @Override
    public Stream<WriteCommand> getConfirmedCommandsStream() {
        return blocks.stream()
                .map(ValidatedBlock::block)
                .map(Block::transactions)
                .flatMap(Collection::stream);
    }

    @Override
    public Block getNextBlock() {
        List<WriteCommand> transactions = commands.stream()
                .limit(16)
                .collect(Collectors.toList());
        if (transactions.size() != 16) {
            return null;
        }
        return new Block(blocks.size(), 0, blocks.get(blocks.size() - 1).hash(), transactions);
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

        return blocks.add(validatedBlock);
    }

    private Account getAccountWith(String accountId) {
        var accounts = getUnconfirmedCommandsStream()
                .filter(CreateAccountCommand.class::isInstance)
                .map(CreateAccountCommand.class::cast)
                .filter(accountCommand -> accountCommand.accountId().equals(accountId))
                .map(accountCommand -> new Account(accountCommand.accountId(), accountCommand.userId()))
                .toList();
        if (accounts.isEmpty()) {
            return null;
        } else {
            return accounts.get(0);
        }
    }

    private Either<Voidy> validate(LoadMoneyCommand command) {
        Account account = getAccountWith(command.accountId());
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
            objOut.writeObject(this);
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
            commands = replicaLedger.commands;
            blocks = replicaLedger.blocks;
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while installing snapshot", e);
        }
    }
}
