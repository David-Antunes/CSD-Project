package com.csd.bftsmart.application.users;

import com.csd.bftsmart.application.entities.User;

import java.util.List;

public interface UserRepository {
    User save(User user);
    List<User> getAll();
}
