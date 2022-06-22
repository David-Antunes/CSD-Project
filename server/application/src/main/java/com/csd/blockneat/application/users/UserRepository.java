package com.csd.blockneat.application.users;

import com.csd.blockneat.application.entities.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    boolean contains(String userId);
}
