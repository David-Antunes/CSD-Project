package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.SOs.AccountSO;
import com.csd.bftsmart.application.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final Map<String, AccountSO> accounts;

    @Autowired
    public AccountRepositoryImpl(Map<String, AccountSO> accounts) {
        this.accounts = accounts;
    }

    @Override
    public AccountSO save(AccountSO accountSO) {
        accounts.put(accountSO.getId(), accountSO);
        return accountSO;
    }

    @Override
    public int updateBalanceById(String id, int value) {
        AccountSO accountSO = accounts.get(id);
        accountSO.setBalance(accountSO.getBalance() + value);
        return accountSO.getBalance();
    }
}
