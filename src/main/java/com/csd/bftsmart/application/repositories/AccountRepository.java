package com.csd.bftsmart.application.repositories;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccountRepository {
    com.csd.bftsmart.infrastructure.entities.Account save(com.csd.bftsmart.infrastructure.entities.Account account);

    int updateBalanceById(String id, int value);
}
