package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

public record GetExtractQuery(String accountId) implements Command<List<Transaction>> {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetExtractQuery, List<Transaction>> {

        private final AccountRepository accounts;

        @Autowired
        Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        public List<Transaction> handle(GetExtractQuery command) {
            return accounts.getExtract(command.accountId);
        }
    }
}
