package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final InMemoryLedger ledger;

    @Autowired
    public AccountRepositoryImpl(InMemoryLedger ledger) {
        this.ledger = ledger;
    }

    private TreeMap<String, User> users() {
        return ledger.getUsers();
    }

    private TreeMap<String, Account> accounts() {
        return ledger.getAccounts();
    }

    private ArrayList<Transaction> transactions() {
        return ledger.getTransactions();
    }

    @Override
    public boolean contains(String accountId) {
        return accounts().containsKey(accountId);
    }
    @Override
    public Account save(Account account) {
        users().get(account.userId().email()).accounts().add(account);
        accounts().put(account.id(), account);
        return account;
    }

    @Override
    public Account get(String accountId) {
        return accounts().get(accountId);
    }

    @Override
    public void updateBalanceById(String id, int value) {
        Account account = accounts().get(id);
        int previousTransactionId = transactions().get(transactions().size() - 1).id();
        transactions().add(new Transaction(previousTransactionId + 1, null, account, value));
    }
    @Override
    public void sendTransaction(String from, String to, int value) {
        Account origin = accounts().get(from);
        Account destination = accounts().get(to);
        int previousTransactionId = transactions().get(transactions().size() - 1).id();
        transactions().add(new Transaction(previousTransactionId + 1, origin, destination, value));
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactions();
    }

    @Override
    public List<Transaction> getExtract(String accountId) {
        return transactions().stream()
                .filter(transaction ->
                        checkTransactionForAccount(transaction.from(), accountId) || checkTransactionForAccount(transaction.to(), accountId))
                .toList();
    }

    private boolean checkTransactionForAccount(Account account, String accountId) {
        return account != null && account.id().equals(accountId);
    }

    @Override
    public int getBalance(String accountId) {
        int balance = 0;
        for (Transaction transaction: transactions()) {
            balance += getTransactionValue(transaction, accountId);
        }
        return balance;
    }

    private int getTransactionValue(Transaction transaction, String accountId) {
        if(accountId == null || accountId.equals(""))
            return 0;

        if (transaction.to() != null && transaction.to().id().equals(accountId)) {
            return transaction.value();
        } else if(transaction.from() != null && transaction.from().id().equals(accountId)) {
            return -transaction.value();
        } else
            return 0;
    }

    @Override
    public int getGlobalValue() {
        int balance = 0;
        for (Transaction transaction: transactions()) {
            balance += transaction.from() == null ? transaction.value() : 0;
        }
        return balance;
    }

    @Override
    public Map<String, Integer> getTotalValue(List<String> accounts) {
        Map<String, Integer> accountValues = new HashMap<>(accounts.size());

        for(String account: accounts)
            accountValues.put(account, 0);
        for(Transaction transaction: transactions()) {
            if(!(transaction.to() == null)) {
                String to = transaction.to().id();
                String from = null;
                if(transaction.from() != null)
                    from = transaction.from().id();
                if(to != null && accountValues.containsKey(to))
                    accountValues.put(to, accountValues.get(to) + getTransactionValue(transaction, to));
                if(from != null && accountValues.containsKey(from))
                    accountValues.put(from, accountValues.get(from) + getTransactionValue(transaction, from));
            }
        }

        return accountValues;
    }
    @Override
    public List<Account> getAll() {
        return new ArrayList<>(accounts().values());
    }

}
