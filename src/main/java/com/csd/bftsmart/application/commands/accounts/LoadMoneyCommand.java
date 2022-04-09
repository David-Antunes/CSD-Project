package com.csd.bftsmart.application.commands.accounts;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public record LoadMoneyCommand(String accountId, int value) implements Command<Voidy> {

    @Component
    public static class Handler implements Command.Handler<LoadMoneyCommand, Voidy> {

        private final AccountService accountService;

        @Autowired
        public Handler(AccountService accountService) {
            this.accountService = accountService;
        }

        @Override
        public Voidy handle(LoadMoneyCommand command) {
            accountService.loadMoney(command.accountId(), command.value());
            return null;
        }
    }
}
