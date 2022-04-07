package com.csd.bftsmart.application.repositories;

import com.csd.bftsmart.application.SOs.AccountSO;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccountRepository {
    AccountSO save(AccountSO account);

    int updateBalanceById(String id, int value);
}
