package com.authservice.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
public class EurekaClientConfig {
    // Eureka istemci yapılandırması application.yml dosyasında tanımlanmıştır
    // Bu sınıf, servis keşfi özelliğini etkinleştirmek için kullanılır
}
