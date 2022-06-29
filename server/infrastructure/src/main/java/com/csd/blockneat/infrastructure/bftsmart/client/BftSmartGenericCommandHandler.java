package com.csd.blockneat.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import com.csd.blockneat.application.entities.Signed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ConditionalOnProperty(name = "bftsmart.enabled")
public abstract class BftSmartGenericCommandHandler<C extends Command<R>, R> implements Command.Handler<C, R> {
    protected final AsynchServiceProxy serviceProxy;

    protected BftSmartGenericCommandHandler(AsynchServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    @Override
    public abstract R handle(C command);

    @Override
    public abstract boolean matches(C command);

    @SuppressWarnings("unchecked")
    protected R handleAs(C command, TOMMessageType reqType) {
        int operationId = -1;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutput objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(command);

            objOut.flush();
            byteOut.flush();

            CompletableFuture<R> consensusReply = new CompletableFuture<>();

            operationId = serviceProxy.invokeAsynchRequest(byteOut.toByteArray(), new ReplyListener() {

                class UnsignedQuorum {
                    private final Map<R, Integer> replies = new ConcurrentHashMap<>();

                    public void processReply(R replyObject, int quorum, RequestContext context) {
                        replies.merge(replyObject, 1, Integer::sum);

                        for (Map.Entry<R, Integer> replyCount : replies.entrySet()) {
                            if (replyCount.getValue() >= quorum) {
                                serviceProxy.cleanAsynchRequest(context.getOperationId());
                                consensusReply.complete(replyCount.getKey());
                                return;
                            }
                        }
                    }

                    public void reset() {
                        replies.clear();
                    }
                }

                class SignedQuorum {
                    private final Map<Object, Map<Integer, String>> signedReplies = new ConcurrentHashMap<>();

                    public void processReply(Signed<?> signed, int quorum, RequestContext context) {
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
                                serviceProxy.cleanAsynchRequest(context.getOperationId());
                                consensusReply.complete((R) new Signed<>(replyCount.getKey(), replyCount.getValue()));
                                return;
                            }
                        }
                    }

                    public void reset() {
                        signedReplies.clear();
                    }
                }

                private final UnsignedQuorum unsignedQuorum = new UnsignedQuorum();

                private final SignedQuorum signedQuorum = new SignedQuorum();

                @Override
                public void reset() {
                    unsignedQuorum.reset();
                    signedQuorum.reset();
                }

                @Override
                public void replyReceived(RequestContext context, TOMMessage reply) {
                    byte[] replyBytes = reply.getContent();
                    try (ByteArrayInputStream byteIn = new ByteArrayInputStream(replyBytes);
                         ObjectInput objIn = new ObjectInputStream(byteIn)) {

                        R replyObject = (R) objIn.readObject();
                        int quorum = (serviceProxy.getViewManager().getCurrentViewN() +
                                serviceProxy.getViewManager().getCurrentViewF()) / 2 + 1;
                        if (replyObject instanceof Signed<?> signed) {
                            signedQuorum.processReply(signed, quorum, context);
                        } else {
                            unsignedQuorum.processReply(replyObject, quorum, context);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        log.warn("Exception handling command: ", e);
                    }
                }
            }, reqType);

            return consensusReply.get(20, TimeUnit.SECONDS);
        } catch (IOException | ExecutionException | InterruptedException e) {
            log.warn("Exception handling command: ", e);
            return null;
        } catch (TimeoutException e) {
            serviceProxy.cleanAsynchRequest(operationId);
            log.warn("Timeout handling command: ", e);
            return null;
        }
    }
}
