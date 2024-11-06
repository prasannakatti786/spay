package com.spay.wallet.config;

import com.spay.wallet.remittance.model.ApiConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    @ConfigurationProperties(prefix = "sandbox.api")
    public ApiConfig apiConfig() {
        return new ApiConfig();
    }

}
