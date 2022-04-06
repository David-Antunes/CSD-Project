package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.repositories.UserRepository;
import com.csd.bftsmart.infrastructure.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository users;

    @Autowired
    public UserRepositoryImpl(UserJpaRepository users) {
        this.users = users;
    }

    @Override
    public User save(User user) {
        return users.save(user);
    }
}
