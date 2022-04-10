package com.csd.bftsmart.infrastructure.bftsmart;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bftsmart")
public record BftSmartProperties(boolean enabled) {
}
