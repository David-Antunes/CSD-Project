package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.SOs.UserSO;
import com.csd.bftsmart.application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<String, UserSO> users;

    @Autowired
    public UserRepositoryImpl(Map<String, UserSO> users) {
        this.users = users;
    }

    @Override
    public UserSO save(UserSO userSO) {
        users.put(userSO.getId(), userSO);
        return userSO;
    }
}
