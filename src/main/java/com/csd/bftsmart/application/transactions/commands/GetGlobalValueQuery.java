package com.csd.bftsmart.application.transactions.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public record GetGlobalValueQuery() implements Command<Integer> {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetGlobalValueQuery, Integer> {

        private final AccountRepository accounts;
        @Autowired
        public Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        public Integer handle(GetGlobalValueQuery command) {
            return accounts.getGlobalValue();
        }
    }
}
