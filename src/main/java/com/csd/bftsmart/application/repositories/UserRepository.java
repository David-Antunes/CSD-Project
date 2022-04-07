package com.csd.bftsmart.application.repositories;

import com.csd.bftsmart.application.SOs.UserSO;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserRepository {
    UserSO save(UserSO userSO);
}
