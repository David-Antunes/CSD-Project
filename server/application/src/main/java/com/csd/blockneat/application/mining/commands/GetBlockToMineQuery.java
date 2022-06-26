package com.csd.blockneat.application.mining.commands;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.ExceptionCode;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.commands.ReadCommand;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.ledger.LedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public record GetBlockToMineQuery() implements Command<Either<Block>>, ReadCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_READ)
    public static class Handler implements Command.Handler<GetBlockToMineQuery, Either<Block>> {

        private final LedgerRepository ledger;

        @Autowired
        public Handler(LedgerRepository ledger) {
            this.ledger = ledger;
        }

        @Override
        public Either<Block> handle(GetBlockToMineQuery command) {
            Block nextBlock = ledger.getNextBlock();
            if (nextBlock == null) {
                return Either.failure(ExceptionCode.NOT_ENOUGH_TRANSACTIONS);
            }
            return Either.success(nextBlock);
        }
    }
}
