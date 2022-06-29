package com.csd.blockneat.application.accounts.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.accounts.AccountRepository;
import com.csd.blockneat.application.commands.ReadCommand;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public record GetBalanceQuery(String accountId) implements Command<Either<Integer>>, ReadCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetBalanceQuery, Either<Integer>> {
        private final AccountRepository accounts;

        @Autowired
        Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        public Either<Integer> handle(GetBalanceQuery command) {
            if(!accounts.containsConfirmed(command.accountId))
                return Either.failure(ExceptionCode.ACCOUNT_EXISTS);
            return Either.success(accounts.getConfirmedBalance(command.accountId));
        }
    }
}
