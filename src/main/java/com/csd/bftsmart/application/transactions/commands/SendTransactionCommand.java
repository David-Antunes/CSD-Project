package com.csd.bftsmart.application.transactions.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.exceptions.Either;
import com.csd.bftsmart.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.rmi.server.ExportException;

public record SendTransactionCommand(String from, String to, int value) implements Command<Either<Voidy>>, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<SendTransactionCommand, Either<Voidy>> {

        private final AccountRepository accounts;

        @Autowired
        public Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        @Override
        public Either<Voidy> handle(SendTransactionCommand command) {
            if(command.value < 0)
                return Either.failure(ExceptionCode.INVALID_VALUE);
            else if(!accounts.contains(command.to) || !accounts.contains(command.from))
                return Either.failure(ExceptionCode.ACCOUNT_DOES_NOT_EXIST);
            else if(accounts.getBalance(command.from) < command.value)
                return Either.failure(ExceptionCode.NOT_ENOUGH_BALANCE);

            accounts.sendTransaction(command.from, command.to, command.value);
            return Either.success();
        }
    }
}
