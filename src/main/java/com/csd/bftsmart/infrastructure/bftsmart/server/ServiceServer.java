package com.csd.bftsmart.infrastructure.bftsmart.server;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import bftsmart.tom.MessageContext;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import com.csd.bftsmart.application.CommandTypes;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "bftsmart", name = "enabled")
public class ServiceServer extends DefaultSingleRecoverable {

    private final Pipeline orderedPipeline, unorderedPipeline;
    private final InMemoryLedger ledger;

    @Autowired
    public ServiceServer(@Qualifier(CommandTypes.APP_WRITE) Pipeline orderedPipeline,
                         @Qualifier(CommandTypes.APP_READ) Pipeline unorderedPipeline,
                         InMemoryLedger ledger) {
        this.orderedPipeline = orderedPipeline;
        this.unorderedPipeline = unorderedPipeline;
        this.ledger = ledger;
    }


    private byte[] appExecute(byte[] command, Pipeline pipeline) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
             ObjectInput objIn = new ObjectInputStream(byteIn);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(((Command) objIn.readObject()).execute(pipeline));
            objOut.flush();
            byteOut.flush();
            return byteOut.toByteArray();
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Occurred during operation execution", e);
            return null;
        }
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext messageContext) {
        return appExecute(command, orderedPipeline);
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext messageContext) {
        return appExecute(command, unorderedPipeline);
    }

    @Override
    public void installSnapshot(byte[] bytes) {
        ledger.installSnapshot(bytes);
    }

    @Override
    public byte[] getSnapshot() {
        return ledger.getSnapshot();
    }
}
