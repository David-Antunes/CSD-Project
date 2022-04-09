package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.repositories.UserRepository;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final InMemoryLedger ledger;

    @Autowired
    public UserRepositoryImpl(InMemoryLedger ledger) {
        this.ledger = ledger;
    }

    @Override
    public User save(User user) {
        ledger.getUsers().put(user.id(), user);
        return user;
    }
}
