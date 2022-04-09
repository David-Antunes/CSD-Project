package com.csd.bftsmart.application.commands.accounts;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.bftsmart.application.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public record CreateAccountCommand(String userId, String accountId) implements Command<Voidy> {

    @Component
    public static class Handler implements Command.Handler<CreateAccountCommand, Voidy> {

        private final AccountService accountService;

        @Autowired
        public Handler(AccountService accountService) {
            this.accountService = accountService;
        }

        @Override
        public Voidy handle(CreateAccountCommand command) {
            accountService.createAccount(command.userId(), command.accountId());
            return null;
        }
    }
}
