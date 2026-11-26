package dev.storylab.storyservice.config;

import dev.storylab.correlationid.common.CorrelationIdInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @Primary
    public RestClient.Builder restClientBuilder(){
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder(CorrelationIdInterceptor correlationIdInterceptor){
        return RestClient.builder()
                .requestInterceptor(correlationIdInterceptor);
    }

    @Bean
    public CorrelationIdInterceptor correlationIdInterceptor(){
        return new CorrelationIdInterceptor();
    }
}
