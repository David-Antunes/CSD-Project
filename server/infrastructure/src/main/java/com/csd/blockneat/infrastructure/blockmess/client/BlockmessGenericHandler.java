package com.csd.blockneat.infrastructure.blockmess.client;

import an.awesome.pipelinr.Command;
import com.csd.blockneat.infrastructure.blockmess.server.BlockmessAppInterface;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
@Qualifier(PipelinrConfig.Blockmess.APP_WRITE)
@ConditionalOnProperty(name = "blockmess.enabled")
public class BlockmessGenericHandler<C extends Command<R>, R> implements Command.Handler<C, R> {

    private final BlockmessAppInterface blockmessAppInterface;

    @Autowired
    public BlockmessGenericHandler(BlockmessAppInterface blockmessAppInterface) {
        this.blockmessAppInterface = blockmessAppInterface;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R handle(C command) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(command);

            objOut.flush();
            byteOut.flush();

            var replyBytes = blockmessAppInterface.invokeSyncOperation(byteOut.toByteArray()).getKey();

            try (ByteArrayInputStream byteIn = new ByteArrayInputStream(replyBytes);
                 ObjectInput objIn = new ObjectInputStream(byteIn)) {

                return (R) objIn.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Exception handling command: ", e);
            return null;
        }
    }

    @Override
    public boolean matches(C command) {
        return true;
    }
}
