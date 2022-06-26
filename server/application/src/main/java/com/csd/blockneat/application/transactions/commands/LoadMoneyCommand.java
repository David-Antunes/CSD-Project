package com.csd.blockneat.application.transactions.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.accounts.AccountRepository;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.crypto.ECDSA;
import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public record LoadMoneyCommand(String accountId, int value,
                               String signBase64) implements Command<Either<Voidy>>, WriteCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<LoadMoneyCommand, Either<Voidy>> {

        private final AccountRepository accounts;

        @Autowired
        public Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        @Override
        public Either<Voidy> handle(LoadMoneyCommand command) {
            Account account = accounts.getUnconfirmed(command.accountId);
            if (account == null)
                return Either.failure(ExceptionCode.ACCOUNT_DOES_NOT_EXIST);
            if (!ECDSA.verifySign(account.userId().base64pk(), command.signBase64, command.accountId + command.value))
                return Either.failure(ExceptionCode.INVALID_SIGNATURE);
            else if (command.value < 0)
                return Either.failure(ExceptionCode.INVALID_VALUE);

            return Either.success();
        }
    }
}
