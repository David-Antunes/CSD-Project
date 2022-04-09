package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accounts;

    @Autowired
    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public void createAccount(String userId, String accountId) {
        Account account = new Account(accountId, userId);
        accounts.save(account);
    }

    public void loadMoney(String id, int value) {
        accounts.updateBalanceById(id, value);
    }

    public void sendTransaction(Account origin, Account destination, int value) {
        //TODO
    }

    public int extractBalance(Account account) {
        return 0; //TODO
    }

    public int getExtract(Account account) {
        return 0; //TODO
    }

    public int getTotalValue(Account[] accounts) {
        return 0; //TODO
    }

    public int getGlobalLedgerValue() {
        return 0; //TODO
    }
}

