package com.csd.bftsmart.rest.controllers;

import com.csd.bftsmart.application.services.UserService;
import com.csd.bftsmart.rest.models.UserRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void createUser(@RequestBody UserRequestModel userRequest) {
        userService.createUser(userRequest.getUserId());
    }

}
