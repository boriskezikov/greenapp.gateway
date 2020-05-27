package com.greenapp.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class CloudConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r
                        .path("/auth/**")
                        .filters(defaultFilters())
                        .uri("http://localhost:8080/")
                        .id("authModule"))
                .route(r -> r
                        .path("/task-provider/**")
                        .filters(defaultFilters())
                        .uri("http://localhost:8199/")
                        .id("taskProviderModule"))
                .build();
    }

    private Function<GatewayFilterSpec, UriSpec> defaultFilters() {
        return f -> f.preserveHostHeader().filter(this::addHeaders);
    }

    private Mono<Void> addHeaders(ServerWebExchange exchange, GatewayFilterChain chain) {
        MultiValueMap<String, String> map = new HttpHeaders();
        map.add("myNewHeader", "value");
        exchange.getResponse()
                .getHeaders()
                .addAll(map);

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("test-header", "123456")
                .build();

        exchange.mutate()
                .request(request)
                .build();

        return chain.filter(exchange);
    }
}