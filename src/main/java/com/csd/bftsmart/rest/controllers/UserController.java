package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.users.commands.CreateUserCommand;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import com.csd.bftsmart.rest.requests.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
