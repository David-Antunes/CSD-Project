package com.csd.bftsmart.infrastructure.bftsmart;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "bftsmart")
public record BftSmartProperties(boolean enabled) {
}
