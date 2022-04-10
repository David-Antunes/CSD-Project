package com.csd.bftsmart.application.users.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.users.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

public record GetAllUsersCommand() implements Command<List<User>> {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<GetAllUsersCommand, List<User>> {

        private final UserRepository users;


        public Handler(UserRepository users) {
            this.users = users;
        }

        @Override
        public List<User> handle(GetAllUsersCommand command) {
            return users.getAll();
        }
    }
}
