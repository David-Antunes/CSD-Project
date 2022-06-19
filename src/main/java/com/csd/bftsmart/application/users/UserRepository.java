package com.csd.bftsmart.application.users;

import com.csd.bftsmart.application.entities.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    boolean contains(String userId);
}
