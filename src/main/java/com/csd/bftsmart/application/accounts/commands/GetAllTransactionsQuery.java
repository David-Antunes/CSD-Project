package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.CommandHandlers;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.entities.Account;
import com.csd.bftsmart.application.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

public record GetAllTransactionsQuery() implements Command<List<Transaction>> {
    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetAllTransactionsQuery, List<Transaction>> {

        private final AccountRepository accounts;
        @Autowired
        public Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }
        @Override
        public List<Transaction> handle(GetAllTransactionsQuery command) {
            return accounts.getAllTransactions();
        }
    }
}
