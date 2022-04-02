package com.csd.bftsmart.application.repositories;

import com.csd.bftsmart.domain.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
