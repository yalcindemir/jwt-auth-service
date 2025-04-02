package com.authservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("gateway") // Bu yapılandırma sadece gateway profili aktif olduğunda kullanılacak
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_service_route", r -> r
                        .path("/auth-service/**")
                        .filters(f -> f
                                .rewritePath("/auth-service/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Source", "API-Gateway"))
                        .uri("lb://auth-service"))
                .build();
    }
}
