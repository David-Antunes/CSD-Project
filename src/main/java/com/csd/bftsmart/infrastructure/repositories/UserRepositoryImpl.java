package com.csd.bftsmart.infrastructure.repositories;

import com.csd.bftsmart.application.SOs.UserSO;
import com.csd.bftsmart.application.repositories.UserRepository;
import com.csd.bftsmart.infrastructure.entities.User;
import com.csd.bftsmart.infrastructure.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository users;
    private final UserMapper userMapper;

    @Autowired
    public UserRepositoryImpl(UserJpaRepository users, UserMapper userMapper) {
        this.users = users;
        this.userMapper = userMapper;
    }

    @Override
    public UserSO save(UserSO userSO) {
        User user = userMapper.userSOToUser(userSO);
        return userMapper.userToUserSO(users.save(user));
    }
}
