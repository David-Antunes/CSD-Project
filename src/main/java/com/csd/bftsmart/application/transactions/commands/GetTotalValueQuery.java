package com.csd.bftsmart.application.transactions.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.application.accounts.AccountRepository;
import com.csd.bftsmart.application.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record GetTotalValueQuery(List<String> accounts) implements Command<Map<String, Integer>>, Serializable {
    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetTotalValueQuery, Map<String, Integer>> {

        private final AccountRepository accounts;
        @Autowired
        public Handler(AccountRepository accounts) {
            this.accounts = accounts;
        }

        public Map<String, Integer> handle(GetTotalValueQuery command) {
            return accounts.getTotalValue(command.accounts);
        }
    }
}
