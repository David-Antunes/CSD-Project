package com.csd.blockneat.application.users.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.CommandTypes;
import com.csd.blockneat.application.commands.ReadCommand;
import com.csd.blockneat.application.entities.User;
import com.csd.blockneat.application.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

public record GetAllUsersQuery() implements Command<List<User>>, ReadCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetAllUsersQuery, List<User>> {

        private final UserRepository users;

        @Autowired
        public Handler(UserRepository users) {
            this.users = users;
        }

        @Override
        public List<User> handle(GetAllUsersQuery command) {
            return users.getAll();
        }
    }
}
