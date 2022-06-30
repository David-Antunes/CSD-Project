package com.csd.blockneat.infrastructure.blockmess.server;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import applicationInterface.ApplicationInterface;
import applicationInterface.ReplyListener;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.entities.Signed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@ConditionalOnProperty(name = "blockmess.enabled")
public class BlockmessAppInterface extends ApplicationInterface {

    private final Pipeline pipeline;

    @Autowired
    public BlockmessAppInterface(@Qualifier(CommandTypes.APP_WRITE) Pipeline pipeline) {
        super(new String[]{});
        this.pipeline = pipeline;
    }

    @Override
    public byte[] processOperation(byte[] command) {
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
}
