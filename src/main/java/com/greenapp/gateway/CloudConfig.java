package com.greenapp.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.logging.Logger;

@Configuration
public class CloudConfig {

    private static final Logger LOG = Logger.getLogger(CloudConfig.class.getName());

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

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-GREEN-APP-ID", "GREEN")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("green", "greenapp"))
                .build();

        LOG.info(String.format("Accepting request: %s", request.getURI()));
        LOG.info(String.format("Headers: %s", request.getHeaders()));
        LOG.info(String.format("Cookies: %s", request.getCookies()));

        exchange.mutate()
                .request(request)
                .build();

        return chain.filter(exchange);
    }
}