package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.users.UserRepository;
import com.csd.bftsmart.exceptions.Either;
import com.csd.bftsmart.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public record CreateAccountCommand(String userId, String accountId) implements Command<Either<Voidy>>, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<CreateAccountCommand, Either<Voidy>> {

        private final AccountRepository accounts;
        private final UserRepository users;

        @Autowired
        public Handler(AccountRepository accounts, UserRepository users) {
            this.accounts = accounts;
            this.users = users;
        }

        @Override
        public Either<Voidy> handle(CreateAccountCommand command) {

            if(!users.contains(command.userId))
                return Either.failure(ExceptionCode.USER_DOES_NOT_EXIST);
            else if(accounts.contains(command.accountId))
                return Either.failure(ExceptionCode.ACCOUNT_EXISTS);
            
            Account account = new Account(command.accountId(), command.userId());
            accounts.save(account);
            return new Either<>(ExceptionCode.SUCCESS, null);
        }
    }
}
