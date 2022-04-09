package com.csd.bftsmart.application.commands.users;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public record CreateUserCommand(String userId) implements Command<Voidy> {

    @Component
    public static class Handler implements Command.Handler<CreateUserCommand, Voidy> {

        private final UserService userService;

        @Autowired
        public Handler(UserService userService) {
            this.userService = userService;
        }

        @Override
        public Voidy handle(CreateUserCommand command) {
            userService.createUser(command.userId());
            return null;
        }
    }
}
