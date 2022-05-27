package com.csd.bftsmart.infrastructure.bftsmart.client;

import an.awesome.pipelinr.Command;
import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.*;
import java.util.Map;
import java.util.concurrent.*;

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

                private final Map<R, Integer> replies = new ConcurrentHashMap<>();

                @Override
                public void reset() {
                    replies.clear();
                }

                @Override
                public void replyReceived(RequestContext context, TOMMessage reply) {
                    byte[] replyBytes = reply.getContent();
                    try (ByteArrayInputStream byteIn = new ByteArrayInputStream(replyBytes);
                         ObjectInput objIn = new ObjectInputStream(byteIn)) {

                        R replyObject = (R) objIn.readObject();
                        replies.merge(replyObject, 1, Integer::sum);
                    } catch (IOException | ClassNotFoundException e) {
                        log.warn("Exception handling command: ", e);
                        return;
                    }

                    int quorum = (serviceProxy.getViewManager().getCurrentViewN() +
                            serviceProxy.getViewManager().getCurrentViewF()) / 2 + 1;

                    for (Map.Entry<R, Integer> replyCount : replies.entrySet()) {
                        if (replyCount.getValue() >= quorum) {
                            serviceProxy.cleanAsynchRequest(context.getOperationId());
                            consensusReply.complete(replyCount.getKey());
                            return;
                        }
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
