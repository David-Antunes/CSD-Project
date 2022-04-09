package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.entities.Account;

public interface AccountService {
    void createAccount(String userId, String accountId);

    void loadMoney(String id, int value);

    void sendTransaction(Account origin, Account destination, int value);

    int extractBalance(Account account);

    int getExtract(Account account);

    int getTotalValue(Account[] accounts);

    int getGlobalLedgerValue();
}
