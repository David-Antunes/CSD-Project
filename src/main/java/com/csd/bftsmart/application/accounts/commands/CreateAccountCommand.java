package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public record CreateAccountCommand(String userId, String accountId) implements Command<Voidy>, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<CreateAccountCommand, Voidy> {

        private final AccountRepository accounts;

        @Autowired
        public Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        @Override
        public Voidy handle(CreateAccountCommand command) {
            Account account = new Account(command.accountId(), command.userId());
            accounts.save(account);
            return null;
        }
    }
}
