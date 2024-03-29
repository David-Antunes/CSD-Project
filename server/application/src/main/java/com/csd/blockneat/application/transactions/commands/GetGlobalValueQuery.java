package com.csd.blockneat.application.transactions.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.accounts.AccountRepository;
import com.csd.blockneat.application.commands.ReadCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public record GetGlobalValueQuery() implements Command<Integer>, ReadCommand, Serializable {

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
