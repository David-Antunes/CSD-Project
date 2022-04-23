package com.csd.bftsmart.application.transactions.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.commands.WriteCommand;
import com.csd.bftsmart.application.crypto.ECDSA;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.exceptions.Either;
import com.csd.bftsmart.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public record SendTransactionCommand(String from, String to, int value,
                                     String signBase64) implements Command<Either<Voidy>>, WriteCommand, Serializable {

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

            if (command.value < 0)
                return Either.failure(ExceptionCode.INVALID_VALUE);

            Account account = accounts.get(command.from);
            if(account == null)
                return Either.failure(ExceptionCode.ACCOUNT_DOES_NOT_EXIST);

            if (!ECDSA.verifySign(account.userId().base64pk(), command.signBase64, command.from + command.to + command.value))
                return Either.failure(ExceptionCode.INVALID_SIGNATURE);
            if (command.to.equals(command.from))
                return Either.failure(ExceptionCode.SAME_ACCOUNT);
            if (!accounts.contains(command.to))
                return Either.failure(ExceptionCode.ACCOUNT_DOES_NOT_EXIST);
            if (accounts.getBalance(command.from) < command.value)
                return Either.failure(ExceptionCode.NOT_ENOUGH_BALANCE);

            accounts.sendTransaction(command.from, command.to, command.value);
            return Either.success();
        }
    }
}
