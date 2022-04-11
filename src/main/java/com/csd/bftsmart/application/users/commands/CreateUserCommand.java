package com.csd.bftsmart.application.users.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;

public record CreateUserCommand(String userId) implements Command<Voidy>, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<CreateUserCommand, Voidy> {

        private final UserRepository users;

        @Autowired
        public Handler(UserRepository users) {
            this.users = users;
        }

        @Override
        public Voidy handle(CreateUserCommand command) {
            User user = new User(command.userId(), new ArrayList<>(2));
            users.save(user);
            return null;
        }
    }
}
