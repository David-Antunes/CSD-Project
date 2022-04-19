package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.exceptions.Either;
import com.csd.bftsmart.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public record GetBalanceQuery(String accountId) implements Command<Either<Integer>> {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetBalanceQuery, Either<Integer>> {
        private final AccountRepository accounts;

        @Autowired
        Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        public Either<Integer> handle(GetBalanceQuery command) {
            if(!accounts.contains(command.accountId))
                return Either.failure(ExceptionCode.ACCOUNT_EXISTS);
            return Either.success(accounts.getBalance(command.accountId));
        }
    }
}
