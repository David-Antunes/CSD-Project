package com.csd.bftsmart.application.users;

import com.csd.bftsmart.application.entities.User;

public interface UserRepository {
    User save(User user);
}
