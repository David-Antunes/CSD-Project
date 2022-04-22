package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.users.commands.CreateUserCommand;
import com.csd.bftsmart.application.users.commands.GetAllUsersQuery;
import com.csd.bftsmart.exceptions.HandleWebExceptions;
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
    public void createUser(@RequestBody UserRequest userRequest, @RequestHeader("signature") String signBase64) {

        HandleWebExceptions.resultOrException(
                new CreateUserCommand(userRequest.userId(), signBase64).execute(pipeline)
        );
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new GetAllUsersQuery().execute(pipeline);
    }

}
