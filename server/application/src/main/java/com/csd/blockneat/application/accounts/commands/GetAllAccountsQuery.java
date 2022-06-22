package com.csd.blockneat.application.accounts.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.CommandTypes;
import com.csd.blockneat.application.accounts.AccountRepository;
import com.csd.blockneat.application.commands.ReadCommand;
import com.csd.blockneat.application.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

public record GetAllAccountsQuery() implements Command<List<Account>>, ReadCommand, Serializable {
        @Component
        @Qualifier(CommandTypes.APP_READ)
        public static class Handler implements Command.Handler<GetAllAccountsQuery, List<Account>> {

            private final AccountRepository accounts;

            @Autowired
            public Handler(AccountRepository accounts) {
                this.accounts = accounts;
            }

            @Override
            public List<Account> handle(GetAllAccountsQuery command) {
                return accounts.getAll();
            }
        }

}
