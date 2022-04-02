package com.csd.bftsmart.application.services;

import com.csd.bftsmart.application.repositories.UserRepository;
import com.csd.bftsmart.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;
    @Autowired
    public UserService(UserRepository users) {
        this.users = users;
    }

    public void createUser(User user) {

    }

    public void deleteUser(User user) {

    }
}
