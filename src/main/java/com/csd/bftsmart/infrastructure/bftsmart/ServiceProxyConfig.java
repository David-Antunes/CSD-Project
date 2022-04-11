package com.csd.bftsmart.infrastructure.bftsmart;

import bftsmart.tom.ServiceProxy;
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
    public ServiceProxy serviceProxy(@Value("${bftsmart.replicaId}") int replicaId) {
        return new ServiceProxy(replicaId);
    }
}
