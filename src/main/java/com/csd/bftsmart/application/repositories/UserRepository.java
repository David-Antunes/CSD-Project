package com.csd.bftsmart.application.repositories;

import com.csd.bftsmart.application.SOs.UserSO;

public interface UserRepository {
    UserSO save(UserSO userSO);
}
