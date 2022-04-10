package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public record LoadMoneyCommand(String accountId, int value) implements Command<Voidy> {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<LoadMoneyCommand, Voidy> {

        private final AccountRepository accounts;

        @Autowired
        public Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        @Override
        public Voidy handle(LoadMoneyCommand command) {
            accounts.updateBalanceById(command.accountId(), command.value());
            return null;
        }
    }
}
