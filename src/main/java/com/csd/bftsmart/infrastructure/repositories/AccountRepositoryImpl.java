package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.accounts.commands.CreateAccountCommand;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.application.transactions.commands.LoadMoneyCommand;
import com.csd.bftsmart.application.transactions.commands.SendTransactionCommand;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
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
    public boolean contains(String accountId) {
        return ledger.getCommands().stream()
                .filter(CreateAccountCommand.class::isInstance)
                .map(CreateAccountCommand.class::cast)
                .map(CreateAccountCommand::accountId)
                .anyMatch(accountId::equals);
    }

    @Override
    public Account get(String accountId) {
        var accounts = ledger.getCommands().stream()
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

    @Override
    public List<Transaction> getAllTransactions() {
        return getTransactionStream()
                .toList();
    }

    private Stream<Transaction> getTransactionStream() {
        AtomicInteger id = new AtomicInteger(0); //TODO
        return ledger.getCommands().stream()
                .filter(command -> command instanceof LoadMoneyCommand || command instanceof SendTransactionCommand)
                .map(command -> {
                    if (command instanceof LoadMoneyCommand loadMoneyCommand) {
                        return new Transaction(id.incrementAndGet(), null, get(loadMoneyCommand.accountId()), loadMoneyCommand.value());
                    }
                    Account from = get(((SendTransactionCommand) command).from());
                    Account to = get(((SendTransactionCommand) command).to());
                    int value = ((SendTransactionCommand) command).value();
                    return new Transaction(id.incrementAndGet(), from, to, value);
                });
    }

    @Override
    public List<Transaction> getExtract(String accountId) {
        return getTransactionStream()
                .filter(transaction ->
                        checkTransactionForAccount(transaction.from(), accountId) || checkTransactionForAccount(transaction.to(), accountId))
                .toList();
    }

    private boolean checkTransactionForAccount(Account account, String accountId) {
        return account != null && account.id().equals(accountId);
    }

    @Override
    public int getBalance(String accountId) {
        return getTransactionStream()
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
        return getTransactionStream()
                .filter(transaction -> transaction.from() == null)
                .map(Transaction::value)
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    public Map<String, Integer> getTotalValue(List<String> accounts) {
        return accounts.stream().collect(Collectors.toMap(
                Function.identity(),
                this::getBalance
        ));
    }

    @Override
    public List<Account> getAll() {
        return ledger.getCommands().stream()
                .filter(CreateAccountCommand.class::isInstance)
                .map(CreateAccountCommand.class::cast)
                .map(accountCommand -> new Account(accountCommand.accountId(), accountCommand.userId()))
                .toList();
    }

}
