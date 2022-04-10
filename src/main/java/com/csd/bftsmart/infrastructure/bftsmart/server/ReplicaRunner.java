package com.csd.bftsmart.infrastructure.bftsmart.server;

import bftsmart.tom.ServiceReplica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.security.Security;

@Component
@ConditionalOnProperty(name = "bftsmart.enabled")
public class ReplicaRunner implements CommandLineRunner {

    private final int replicaId;
    private final ServiceServer serviceServer;

    @Autowired
    public ReplicaRunner(@Value("${bftsmart.replicaId}") int replicaId, ServiceServer serviceServer) {
        this.replicaId = replicaId;
        this.serviceServer = serviceServer;
    }

    @Override
    public void run(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        new ServiceReplica(replicaId, serviceServer, serviceServer);
    }
}
