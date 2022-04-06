package com.csd.bftsmart.application.repositories;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserRepository {
    com.csd.bftsmart.infrastructure.entities.User save(com.csd.bftsmart.infrastructure.entities.User user);
}
