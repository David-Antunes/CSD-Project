package com.csd.blockneat.application.middlewares;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.application.ExceptionCode;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.entities.Signed;
import com.csd.blockneat.application.ledger.LedgerRepository;
import com.csd.blockneat.application.Either;
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
        if (command instanceof WriteCommand writeCommand &&
                isSuccessful(response)) {
            ledger.append(writeCommand);
        }
        return response;
    }

    private <R> boolean isSuccessful(R response) {
        Either<?> result;
        if (response instanceof Signed<?> signed && signed.object() instanceof Either<?> either) {
            result = either;
        } else if (response instanceof Either<?> either) {
            result = either;
        } else {
            throw new RuntimeException("Invalid Return");
        }
        return result.left() == ExceptionCode.SUCCESS;
    }
}
