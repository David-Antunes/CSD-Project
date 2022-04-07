package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.SOs.AccountSO;
import com.csd.bftsmart.application.repositories.AccountRepository;
import com.csd.bftsmart.infrastructure.entities.Account;
import com.csd.bftsmart.infrastructure.mappers.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository accounts;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountRepositoryImpl(AccountJpaRepository accounts, AccountMapper accountMapper) {
        this.accounts = accounts;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountSO save(AccountSO accountSO) {
        Account account = accountMapper.accountSOToAccount(accountSO);
        return accountMapper.accountToAccountSO(accounts.save(account));
    }

    @Override
    public int updateBalanceById(String id, int value) {
        return accounts.updateBalanceById(id, value);
    }
}
