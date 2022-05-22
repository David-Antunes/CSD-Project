package com.csd.bftsmart.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.tom.ServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.*;
import java.util.function.Function;

@Slf4j
@ConditionalOnProperty(name = "bftsmart.enabled")
public abstract class BftSmartGenericCommandHandler<C extends Command<R>, R> implements Command.Handler<C, R> {
    protected final ServiceProxy serviceProxy;

    protected BftSmartGenericCommandHandler(ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    @Override
    public abstract R handle(C command);

    @Override
    public abstract boolean matches(C command);

    @SuppressWarnings("unchecked")
    protected R handleAs(C command, Function<byte[], byte[]> serviceProxyInvoker) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(command);

            objOut.flush();
            byteOut.flush();

            byte[] reply = serviceProxyInvoker.apply(byteOut.toByteArray());
            if (reply.length == 0)
                return null;
            try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply);
                 ObjectInput objIn = new ObjectInputStream(byteIn)) {
                return (R) objIn.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Exception handling command: ", e);
            return null;
        }
    }
}
