package com.csd.blockneat.infrastructure.blockmess.client;

import an.awesome.pipelinr.Command;
import applicationInterface.ReplyListener;
import com.csd.blockneat.application.entities.Signed;
import com.csd.blockneat.infrastructure.blockmess.server.BlockmessAppInterface;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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

            CompletableFuture<R> consensusReply = new CompletableFuture<>();



            blockmessAppInterface.invokeAsyncOperation(byteOut.toByteArray(), new ReplyListener() {

                class UnsignedQuorum {
                    private final Map<R, Integer> replies = new ConcurrentHashMap<>();

                    public void processReply(R replyObject, int quorum) {
                        replies.merge(replyObject, 1, Integer::sum);

                        for (Map.Entry<R, Integer> replyCount : replies.entrySet()) {
                            if (replyCount.getValue() >= quorum) {
                                consensusReply.complete(replyCount.getKey());
                                return;
                            }
                        }
                    }
                }

                class SignedQuorum {
                    private final Map<Object, Map<Integer, String>> signedReplies = new ConcurrentHashMap<>();

                    @SuppressWarnings("unchecked")
                    public void processReply(Signed<?> signed, int quorum) {
                        signedReplies.merge(signed.object(), signed.signBase64(),
                                (sign, sign2) -> Stream.concat(sign.entrySet().stream(), sign2.entrySet().stream())
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                (s, s2) -> s,
                                                TreeMap::new
                                        )));

                        for (Map.Entry<Object, Map<Integer, String>> replyCount : signedReplies.entrySet()) {
                            if (replyCount.getValue().size() >= quorum) {
                                consensusReply.complete((R) new Signed<>(replyCount.getKey(), replyCount.getValue()));
                                return;
                            }
                        }
                    }
                }

                private final UnsignedQuorum unsignedQuorum = new UnsignedQuorum();

                private final SignedQuorum signedQuorum = new SignedQuorum();

                @Override
                @SuppressWarnings("unchecked")
                public void processReply(Pair<byte[], Long> pair) {
                    byte[] replyBytes = pair.getKey();
                    try (ByteArrayInputStream byteIn = new ByteArrayInputStream(replyBytes);
                         ObjectInput objIn = new ObjectInputStream(byteIn)) {

                        R replyObject = (R) objIn.readObject();
                        int n = 4, f = 1;
                        int quorum = (n + f) / 2 + 1; //TODO
                        if (replyObject instanceof Signed<?> signed) {
                            signedQuorum.processReply(signed, quorum);
                        } else {
                            unsignedQuorum.processReply(replyObject, quorum);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        log.warn("Exception handling command: ", e);
                    }
                }
            });

            return consensusReply.get(20, TimeUnit.SECONDS);
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
            log.warn("Timeout handling command: ", e);
            return null;
        }
    }

    @Override
    public boolean matches(C command) {
        return true;
    }
}
