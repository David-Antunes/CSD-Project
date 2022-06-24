package com.csd.blockneat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProxyBFT {
    public static void main(String[] args) {
        SpringApplication.run(ProxyBFT.class, args);
    }
}
