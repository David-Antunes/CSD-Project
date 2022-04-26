package com.csd.bftsmart.application.middlewares;

import an.awesome.pipelinr.Command;
import com.csd.bftsmart.application.commands.WriteCommand;
import com.csd.bftsmart.application.ledger.LedgerRepository;
import com.csd.bftsmart.exceptions.Either;
import com.csd.bftsmart.exceptions.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LedgerPersistable implements Command.Middleware {

    private final LedgerRepository ledger;

    @Autowired
    public LedgerPersistable(LedgerRepository ledger) {
        this.ledger = ledger;
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        R response = next.invoke();
        if (command instanceof WriteCommand &&
                ((Either<?>) response).left() == ExceptionCode.SUCCESS) {
            ledger.append((WriteCommand) command);
        }
        return response;
    }
}
