package com.csd.blockneat.application.mining.commands;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.ExceptionCode;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.ledger.LedgerRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@JsonTypeName("ProposeBlockCommand")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProposeBlockCommand(ValidatedBlock block) implements Command<Either<Voidy>>, WriteCommand, Serializable {

    @Component
    @Qualifier(CommandTypes.APP_WRITE)
    public static class Handler implements Command.Handler<ProposeBlockCommand, Either<Voidy>> {

        private final LedgerRepository ledger;

        @Autowired
        public Handler(LedgerRepository ledger) {
            this.ledger = ledger;
        }

        @Override
        public Either<Voidy> handle(ProposeBlockCommand command) {
            if (!ledger.receiveBlock(command.block)) {
                return Either.failure(ExceptionCode.INVALID_BLOCK);
            }
            return Either.success();
        }
    }
}
