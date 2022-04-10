package com.csd.bftsmart.application.accounts;

import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;

import java.util.List;

public interface AccountRepository {
    Account save(Account account);

    void updateBalanceById(String id, int value);

    void sendTransaction(String from, String to, int value);

    List<Transaction> getAllTransactions();

    List<Transaction> getExtract(String accountId);
}
