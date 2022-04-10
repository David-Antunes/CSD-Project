package com.csd.bftsmart;

import com.csd.bftsmart.infrastructure.bftsmart.BftSmartProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BftSmartProperties.class})
public class BftSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(BftSmartApplication.class, args);
    }

}
