package com.csd.blockneat.infrastructure.repositories;

import com.csd.blockneat.application.accounts.AccountRepository;
import com.csd.blockneat.application.accounts.commands.CreateAccountCommand;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.entities.Transaction;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import com.csd.blockneat.application.transactions.commands.SendTransactionCommand;
import com.csd.blockneat.infrastructure.persistence.InMemoryLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final InMemoryLedger ledger;

    @Autowired
    public AccountRepositoryImpl(InMemoryLedger ledger) {
        this.ledger = ledger;
    }

    @Override
    public boolean containsUnconfirmed(String accountId) {
        boolean pending = ledger.getPendingCommandsStream()
                .filter(CreateAccountCommand.class::isInstance)
                .map(CreateAccountCommand.class::cast)
                .map(CreateAccountCommand::accountId)
                .anyMatch(accountId::equals);
        return pending || containsConfirmed(accountId);
    }

    @Override
    public boolean containsConfirmed(String accountId) {
        return ledger.getConfirmed(accountId) != null;
    }

    @Override
    public Account getUnconfirmed(String accountId) {
        return ledger.getUnconfirmed(accountId);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return getConfirmedTransactionStream()
                .toList();
    }

    private Stream<Transaction> getUnconfirmedTransactionStream() {
        return getTransactionStream(ledger.getPendingCommandsStream(), ledger::getUnconfirmed);
    }

    private Stream<Transaction> getConfirmedTransactionStream() {
        return getTransactionStream(ledger.getAllConfirmedTransactions().stream(), ledger::getConfirmed);
    }

    private Stream<Transaction> getTransactionStream(Stream<WriteCommand> commandsStream, Function<String, Account> getAccount) {
        AtomicInteger id = new AtomicInteger(0); //TODO
        return commandsStream
                .filter(command -> command instanceof LoadMoneyCommand || command instanceof SendTransactionCommand)
                .map(command -> {
                    if (command instanceof LoadMoneyCommand loadMoneyCommand) {
                        return new Transaction(id.incrementAndGet(), null, getAccount.apply(loadMoneyCommand.accountId()), loadMoneyCommand.value());
                    }
                    Account from = getAccount.apply(((SendTransactionCommand) command).from());
                    Account to = getAccount.apply(((SendTransactionCommand) command).to());
                    int value = ((SendTransactionCommand) command).value();
                    return new Transaction(id.incrementAndGet(), from, to, value);
                });
    }

    @Override
    public List<Transaction> getExtract(String accountId) {
        return getConfirmedTransactionStream()
                .filter(transaction -> checkTransactionForAccount(transaction.from(), accountId) ||
                        checkTransactionForAccount(transaction.to(), accountId))
                .toList();
    }

    private boolean checkTransactionForAccount(Account account, String accountId) {
        return account != null && account.id().equals(accountId);
    }

    @Override
    public int getUnconfirmedBalance(String accountId) {
        return getBalanceWith(accountId, Stream.concat(getConfirmedTransactionStream(), getUnconfirmedTransactionStream()));
    }

    public int getConfirmedBalance(String accountId) {
        return getBalanceWith(accountId, getConfirmedTransactionStream());
    }

    private int getBalanceWith(String accountId, Stream<Transaction> transactionStream) {
        return transactionStream
                .map(transaction -> getTransactionValue(transaction, accountId))
                .reduce(Integer::sum)
                .orElse(0);
    }

    private int getTransactionValue(Transaction transaction, String accountId) {
        if (accountId == null || accountId.equals(""))
            return 0;

        if (transaction.to() != null && transaction.to().id().equals(accountId)) {
            return transaction.value();
        } else if (transaction.from() != null && transaction.from().id().equals(accountId)) {
            return -transaction.value();
        } else
            return 0;
    }

    @Override
    public int getGlobalValue() {
        return getConfirmedTransactionStream()
                .filter(transaction -> transaction.from() == null)
                .map(Transaction::value)
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    public Map<String, Integer> getTotalValue(List<String> accounts) {
        return accounts.stream().collect(Collectors.toMap(
                Function.identity(),
                this::getConfirmedBalance
        ));
    }

    @Override
    public List<Account> getAll() {
        return ledger.getAllConfirmedAccounts();
    }

}
