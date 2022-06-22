package com.csd.blockneat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BftSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(BftSmartApplication.class, args);
    }

}
