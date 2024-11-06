package com.spay.wallet.customerservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {
    @PostConstruct
    public  void timeZone(){
          TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

}
