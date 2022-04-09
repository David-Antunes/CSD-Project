package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {

    private final UserRepository users;

    @Autowired
    public UserService(UserRepository users) {
        this.users = users;
    }

    public void createUser(String userId) {
        User user = new User(userId, new ArrayList<>(2));
        users.save(user);
    }

}
