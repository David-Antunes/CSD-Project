package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceLocal implements AccountService {

    private final AccountRepository accounts;

    @Autowired
    public AccountServiceLocal(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public void createAccount(String userId, String accountId) {
        Account account = new Account(accountId, userId);
        accounts.save(account);
    }

    @Override
    public void loadMoney(String id, int value) {
        accounts.updateBalanceById(id, value);
    }

    @Override
    public void sendTransaction(Account origin, Account destination, int value) {
        //TODO
    }

    @Override
    public int extractBalance(Account account) {
        return 0; //TODO
    }

    @Override
    public int getExtract(Account account) {
        return 0; //TODO
    }

    @Override
    public int getTotalValue(Account[] accounts) {
        return 0; //TODO
    }

    @Override
    public int getGlobalLedgerValue() {
        return 0; //TODO
    }
}

