package com.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiClientConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // API Gateway üzerinden diğer servislere istek yapmak için kullanılacak
        return restTemplate;
    }
}
