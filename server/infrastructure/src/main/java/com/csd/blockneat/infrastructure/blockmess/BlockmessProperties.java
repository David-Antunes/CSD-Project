package com.csd.blockneat.infrastructure.blockmess;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blockmess")
public record BlockmessProperties(boolean enabled, int replicaId) {
}
