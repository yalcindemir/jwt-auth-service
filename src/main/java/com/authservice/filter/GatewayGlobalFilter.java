package com.authservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("gateway")
@Slf4j
public class GatewayGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // İstek bilgilerini logla
        log.info("Gateway Filter: Request URI: {}", request.getURI());
        
        // MAC adresi header'ı varsa logla
        String macAddress = request.getHeaders().getFirst("X-MAC-Address");
        if (macAddress != null && !macAddress.isEmpty()) {
            log.info("Gateway Filter: MAC Address: {}", macAddress);
        }
        
        // İsteği değiştirmeden devam et
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Filtreleme sırasını belirler, düşük değerler önce çalışır
        return -1;
    }
}
