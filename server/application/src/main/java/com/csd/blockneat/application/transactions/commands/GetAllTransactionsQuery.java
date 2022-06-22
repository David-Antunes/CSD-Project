package com.csd.blockneat.application.transactions.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.CommandTypes;
import com.csd.blockneat.application.accounts.AccountRepository;
import com.csd.blockneat.application.commands.ReadCommand;
import com.csd.blockneat.application.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

public record GetAllTransactionsQuery() implements Command<List<Transaction>>, ReadCommand, Serializable {
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
