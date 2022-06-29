package com.csd.blockneat.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.core.messages.TOMMessageType;
import com.csd.blockneat.application.commands.ReadCommand;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Qualifier(PipelinrConfig.BFT_SMART_APP_READ)
@ConditionalOnProperty(name = "bftsmart.enabled")
public class BftSmartReadCommandHandler<C extends Command<R>, R> extends BftSmartGenericCommandHandler<C, R> {

    @Autowired
    public BftSmartReadCommandHandler(AsynchServiceProxy serviceProxy) {
        super(serviceProxy);
    }

    @Override
    public R handle(C command) {
        return handleAs(command, TOMMessageType.UNORDERED_REQUEST);
    }

    @Override
    public boolean matches(C command) {
        return command instanceof ReadCommand;
    }
}
