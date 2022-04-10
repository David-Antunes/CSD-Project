package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.users.commands.CreateUserCommand;
import com.csd.bftsmart.application.users.commands.GetAllUsersCommand;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import com.csd.bftsmart.rest.requests.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Pipeline pipeline;

    @Autowired
    public UserController(@Qualifier(PipelinrConfig.CONTROLLER_PIPELINE) Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping
    public void createUser(@RequestBody UserRequest userRequest) {
        new CreateUserCommand(userRequest.userId()).execute(pipeline);
    }
    // DEBUGGING/TESTING ENDPOINT
    @GetMapping
    public List<User> getAllUsers() {
        return new GetAllUsersCommand().execute(pipeline);
    }

}
