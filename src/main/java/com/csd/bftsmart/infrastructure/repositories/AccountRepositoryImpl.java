package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.TreeMap;

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
    public Account save(Account account) {
        users().get(account.userId()).accounts().add(account);
        accounts().put(account.id(), account);
        return account;
    }

    @Override
    public void updateBalanceById(String id, int value) {
        Account account = accounts().get(id);
        int previousTransactionId = transactions().get(transactions().size() - 1).id();
        transactions().add(new Transaction(previousTransactionId + 1, null, account, value));
    }
}
