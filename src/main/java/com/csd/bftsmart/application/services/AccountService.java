package com.csd.bftsmart.application.services;

import com.csd.bftsmart.infrastructure.repositories.AccountRepository;
import com.csd.bftsmart.domain.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accounts;

    @Autowired
    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public void createAccount(Account account) {

    }
    public void deleteAccount(Account account) {

    }

    public void loadMoney(Account account, int value) {

    }

    public void sendTransaction(Account origin, Account destination, int value) {

    }

    public int extractBalance(Account account) {
        return 0;
    }
    public int getExtract(Account account) {
        return 0;
    }

    public int getTotalValue(Account[] accounts) {
        return 0;

    }

    public int getGlobalLedgerValue() {
        return 0;
    }
}

