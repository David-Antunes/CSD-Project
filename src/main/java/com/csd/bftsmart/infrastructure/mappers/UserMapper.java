package com.csd.bftsmart.infrastructure.mappers;

import com.csd.bftsmart.infrastructure.entities.User;
import com.csd.bftsmart.application.SOs.UserSO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userSOToUser(UserSO userSO);

    UserSO userToUserSO(User user);
}
