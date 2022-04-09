package com.csd.bftsmart.application.repositories;

import com.csd.bftsmart.application.entities.User;

public interface UserRepository {
    User save(User user);
}
