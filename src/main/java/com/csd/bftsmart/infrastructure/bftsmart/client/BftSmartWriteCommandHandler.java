package com.csd.bftsmart.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.tom.ServiceProxy;
import com.csd.bftsmart.application.commands.WriteCommand;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Qualifier(PipelinrConfig.BFT_SMART_APP_WRITE)
@ConditionalOnProperty(name = "bftsmart.enabled")
public class BftSmartWriteCommandHandler<C extends Command<R>, R> extends BftSmartGenericCommandHandler<C, R> {

    @Autowired
    public BftSmartWriteCommandHandler(ServiceProxy serviceProxy) {
        super(serviceProxy);
    }

    @Override
    public R handle(C command) {
        return handleAs(command, serviceProxy::invokeOrdered);
    }

    @Override
    public boolean matches(C command) {
        return command instanceof WriteCommand;
    }
}
