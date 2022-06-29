package com.csd.blockneat.application.transactions.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.accounts.AccountRepository;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.crypto.ECDSA;
import com.csd.blockneat.application.entities.Account;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.ExceptionCode;
import com.csd.blockneat.application.entities.Signed;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@JsonTypeName("transaction")
@JsonIgnoreProperties(ignoreUnknown = true)
public record SendTransactionCommand(String from, String to, int value,
                                     String signBase64, long timestamp) implements Command<Signed<Either<Integer>>>, WriteCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<SendTransactionCommand, Signed<Either<Integer>>> {

        private final AccountRepository accounts;
        private final ECDSA ecdsa;

        @Autowired
        public Handler(AccountRepository accounts, ECDSA ecdsa) {
            this.accounts = accounts;
            this.ecdsa = ecdsa;
        }

        @Override
        public Signed<Either<Integer>> handle(SendTransactionCommand command) {

            if (command.value < 0)
                return ecdsa.of(Either.failure(ExceptionCode.INVALID_VALUE));

            Account account = accounts.getUnconfirmed(command.from);
            if (account == null)
                return ecdsa.of(Either.failure(ExceptionCode.ACCOUNT_DOES_NOT_EXIST));

            if (!ECDSA.verifySign(account.userId().base64pk(), command.signBase64, command.from + command.to + command.value))
                return ecdsa.of(Either.failure(ExceptionCode.INVALID_SIGNATURE));
            if (command.to.equals(command.from))
                return ecdsa.of(Either.failure(ExceptionCode.SAME_ACCOUNT));
            if (!accounts.containsUnconfirmed(command.to))
                return ecdsa.of(Either.failure(ExceptionCode.ACCOUNT_DOES_NOT_EXIST));
            int remaining = accounts.getUnconfirmedBalance(command.from) - command.value;
            if (remaining < 0)
                return ecdsa.of(Either.failure(ExceptionCode.NOT_ENOUGH_BALANCE));

            return ecdsa.of(Either.success(remaining));
        }
    }
}
