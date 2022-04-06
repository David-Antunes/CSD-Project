package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.infrastructure.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, String> {

    @Transactional
    @Modifying
    @Query("update Account a set a.balance = a.balance + :value where a.id = :id")
    int updateBalanceById(String id, int value);
}
