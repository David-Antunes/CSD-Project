package com.csd.bftsmart.infrastructure.config;

import com.csd.bftsmart.application.SOs.AccountSO;
import com.csd.bftsmart.application.SOs.UserSO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

@Configuration
public class MapConfig {

    @Bean
    public Map<String, AccountSO> accountMapBean() {
        return new TreeMap<>();
    }

    @Bean
    public Map<String, UserSO> userMapBean() {
        return new TreeMap<>();
    }
}
