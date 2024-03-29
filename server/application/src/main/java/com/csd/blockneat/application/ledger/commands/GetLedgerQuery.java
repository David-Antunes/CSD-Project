package com.csd.blockneat.application.ledger.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.commands.ReadCommand;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.ledger.LedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

public record GetLedgerQuery() implements Command<List<ValidatedBlock>>, ReadCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetLedgerQuery, List<ValidatedBlock>> {

        private final LedgerRepository ledger;
        @Autowired
        Handler(LedgerRepository ledger) {
            this.ledger = ledger;
        }

        public List<ValidatedBlock> handle(GetLedgerQuery command) {
            return ledger.getBlocks();
        }
    }
}
