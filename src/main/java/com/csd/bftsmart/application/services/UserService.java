package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;

    @Autowired
    public UserService(UserRepository users) {
        this.users = users;
    }

    public void createUser(String userId) {
        com.csd.bftsmart.infrastructure.entities.User user = new com.csd.bftsmart.infrastructure.entities.User();
        user.setId(userId);
        users.save(user);
    }

}
