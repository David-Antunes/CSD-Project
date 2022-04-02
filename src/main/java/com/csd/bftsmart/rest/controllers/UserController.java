package com.csd.bftsmart.rest.controllers;

import com.csd.bftsmart.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
}
