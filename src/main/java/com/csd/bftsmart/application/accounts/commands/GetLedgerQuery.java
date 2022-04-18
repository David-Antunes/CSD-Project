package com.csd.bftsmart.application.accounts.commands;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public record GetLedgerQuery() implements Command<InMemoryLedger> {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetLedgerQuery, InMemoryLedger> {

        private final InMemoryLedger ledger;
        @Autowired
        Handler(InMemoryLedger ledger) {
            this.ledger = ledger;
        }

        public InMemoryLedger handle(GetLedgerQuery command) {
            return this.ledger;
        }
    }
}
