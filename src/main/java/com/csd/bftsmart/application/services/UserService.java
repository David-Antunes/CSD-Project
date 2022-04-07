package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.SOs.UserSO;
import com.csd.bftsmart.application.repositories.UserRepository;
import com.csd.bftsmart.infrastructure.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;

    @Autowired
    public UserService(UserRepository users, UserMapper userMapper) {
        this.users = users;
    }

    public void createUser(String userId) {
        UserSO userSO = new UserSO(userId);
        users.save(userSO);
    }

}
