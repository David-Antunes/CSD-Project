package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public record GetBalanceQuery(String accountId) implements Command<Integer> {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetBalanceQuery, Integer> {
        private final AccountRepository accounts;

        @Autowired
        Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        public Integer handle(GetBalanceQuery command) {
            return accounts.getBalance(command.accountId);
        }
    }
}
