package com.csd.bftsmart.application.users.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.commands.WriteCommand;
import com.csd.bftsmart.application.crypto.ECDSA;
import com.csd.bftsmart.exceptions.Either;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.entities.User;
import com.csd.bftsmart.application.users.UserRepository;
import com.csd.bftsmart.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;

public record CreateUserCommand(User.Id userId, String signBase64) implements Command<Either<Voidy>>, WriteCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<CreateUserCommand, Either<Voidy>> {

        private final UserRepository users;

        @Autowired
        public Handler(UserRepository users) {
            this.users = users;
        }

        @Override
        public Either<Voidy> handle(CreateUserCommand command) {
            if(!ECDSA.verifySign(command.userId.base64pk(), command.signBase64, command.userId.email()))
                return Either.failure(ExceptionCode.INVALID_SIGNATURE);
            if(users.contains(command.userId.email()))
                return Either.failure(ExceptionCode.USER_EXISTS);

            User user = new User(command.userId(), new ArrayList<>(2));
            users.save(user);
            return Either.success();
        }
    }
}
