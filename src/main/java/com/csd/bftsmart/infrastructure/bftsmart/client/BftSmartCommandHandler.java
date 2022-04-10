package com.csd.bftsmart.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.tom.ServiceProxy;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

@Slf4j
@Component
@Qualifier(PipelinrConfig.BFT_SMART_APP_WRITE)
@ConditionalOnProperty(prefix = "bftsmart", name = "enabled")
public class BftSmartCommandHandler<C extends Command<R>, R> implements Command.Handler<C, R> {

    private final ServiceProxy serviceProxy;

    @Autowired
    public BftSmartCommandHandler(ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    @Override
    public R handle(C command) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(command);

            objOut.flush();
            byteOut.flush();

            //TODO add etiquette interface to C to select invoke method
            serviceProxy.invokeOrdered(byteOut.toByteArray());

        } catch (IOException e) {
            log.warn("Exception putting value into map: ", e);
        }
        return null;
    }

}
