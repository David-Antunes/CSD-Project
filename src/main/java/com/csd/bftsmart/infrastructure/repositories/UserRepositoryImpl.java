package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.users.UserRepository;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final InMemoryLedger ledger;

    @Autowired
    public UserRepositoryImpl(InMemoryLedger ledger) {
        this.ledger = ledger;
    }

    @Override
    public User save(User user) {
        ledger.getUsers().put(user.id().email(), user);
        return user;
    }

    public boolean contains(String userId) {
        return ledger.getUsers().containsKey(userId);
    }
    @Override
    public List<User> getAll() {
        return new ArrayList<>(ledger.getUsers().values());
    }

}
