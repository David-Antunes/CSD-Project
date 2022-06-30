package com.csd.blockneat.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.core.messages.TOMMessageType;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Qualifier(PipelinrConfig.BftSmart.APP_WRITE)
@ConditionalOnProperty(name = "bftsmart.enabled")
public class BftSmartWriteCommandHandler<C extends Command<R>, R> extends BftSmartGenericCommandHandler<C, R> {

    @Autowired
    public BftSmartWriteCommandHandler(AsynchServiceProxy serviceProxy) {
        super(serviceProxy);
    }

    @Override
    public R handle(C command) {
        return handleAs(command, TOMMessageType.ORDERED_REQUEST);
    }

    @Override
    public boolean matches(C command) {
        return command instanceof WriteCommand;
    }
}
