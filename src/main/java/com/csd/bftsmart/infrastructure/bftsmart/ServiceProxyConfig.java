package com.csd.bftsmart.infrastructure.bftsmart;

import bftsmart.tom.ServiceProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "bftsmart", name = "enabled")
public class ServiceProxyConfig {

    @Bean
    public ServiceProxy serviceProxy() {
        return new ServiceProxy(1); //TODO
    }
}
