package dev.storylab.userservice.config;

import dev.storylab.correlationid.common.CorrelationIdFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorrelationIdConfig {
    @Bean
    public CorrelationIdFilter  correlationIdFilter(){
        return new CorrelationIdFilter();
    }
}
