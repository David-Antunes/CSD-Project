package com.csd.bftsmart.application.transactions.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.commands.ReadCommand;
import com.csd.bftsmart.application.entities.Transaction;
import com.csd.bftsmart.exceptions.Either;
import com.csd.bftsmart.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

public record GetExtractQuery(String accountId) implements Command<Either<List<Transaction>>>, ReadCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetExtractQuery, Either<List<Transaction>>> {

        private final AccountRepository accounts;

        @Autowired
        Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        public Either<List<Transaction>> handle(GetExtractQuery command) {

            if(!accounts.contains(command.accountId))
                return Either.failure(ExceptionCode.ACCOUNT_EXISTS);

            return Either.success(accounts.getExtract(command.accountId));
        }
    }
}
