package com.csd.bftsmart.application.accounts;

import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;

import java.util.List;
import java.util.Map;

public interface AccountRepository {
    Account save(Account account);

    void updateBalanceById(String id, int value);

    void sendTransaction(String from, String to, int value);

    List<Transaction> getAllTransactions();

    List<Transaction> getExtract(String accountId);

    int getBalance(String accountId);

    int getGlobalValue();

    Map<Account, Integer> getTotalValue(List<Account> accounts);

    List<Account> getAll();

    boolean contains(String accountId);

    Account get(String accountId);
}
