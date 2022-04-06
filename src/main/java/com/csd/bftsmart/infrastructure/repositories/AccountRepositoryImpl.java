package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.repositories.AccountRepository;
import com.csd.bftsmart.infrastructure.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository accounts;

    @Autowired
    public AccountRepositoryImpl(AccountJpaRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public Account save(Account account) {
        return accounts.save(account);
    }

    @Override
    public int updateBalanceById(String id, int value) {
        return accounts.updateBalanceById(id, value);
    }
}
