package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.SOs.AccountSO;
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
        AccountSO accountSO = new AccountSO(accountId, 0, userId);
        accounts.save(accountSO);
    }

    public void loadMoney(String id, int value) {
        accounts.updateBalanceById(id, value);
    }

    public void sendTransaction(AccountSO origin, AccountSO destination, int value) {
        //TODO
    }

    public int extractBalance(AccountSO account) {
        return 0; //TODO
    }

    public int getExtract(AccountSO account) {
        return 0; //TODO
    }

    public int getTotalValue(AccountSO[] accounts) {
        return 0; //TODO
    }

    public int getGlobalLedgerValue() {
        return 0; //TODO
    }
}

