package com.csd.blockneat.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.blockneat.application.entities.User;
import com.csd.blockneat.application.users.commands.CreateUserCommand;
import com.csd.blockneat.application.users.commands.GetAllUsersQuery;
import com.csd.blockneat.rest.exceptions.HandleWebExceptions;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import com.csd.blockneat.rest.requests.UserRequest;
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
