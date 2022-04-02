package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.domain.entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, String>, com.csd.bftsmart.application.repositories.AccountRepository {

}
