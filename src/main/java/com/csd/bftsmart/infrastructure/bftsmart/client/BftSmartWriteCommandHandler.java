package com.csd.bftsmart.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.tom.ServiceProxy;
import com.csd.bftsmart.application.commands.WriteCommand;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
@Qualifier(PipelinrConfig.BFT_SMART_APP_WRITE)
@ConditionalOnProperty(name = "bftsmart.enabled")
public class BftSmartWriteCommandHandler<C extends Command<R>, R> implements Command.Handler<C, R> {

    private final ServiceProxy serviceProxy;

    @Autowired
    public BftSmartWriteCommandHandler(ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R handle(C command) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(command);

            objOut.flush();
            byteOut.flush();

            byte[] reply = serviceProxy.invokeOrdered(byteOut.toByteArray());
            if (reply.length == 0)
                return null;
            try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
                 ObjectInput objIn = new ObjectInputStream(byteIn)) {
                return (R)objIn.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Exception handling command: ", e);
            return null;
        }
    }

    @Override
    public boolean matches(C command) {
        return command instanceof WriteCommand;
    }
}
