package com.csd.blockneat.infrastructure.bftsmart;

import bftsmart.tom.AsynchServiceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ConditionalOnProperty(name = "bftsmart.enabled")
public class ServiceProxyConfig {

    @Bean
    @DependsOn({"replicaRunner"})
    public AsynchServiceProxy asynchServiceProxy(@Value("${bftsmart.replicaId}") int replicaId) {
        return new AsynchServiceProxy(replicaId);
    }
}
