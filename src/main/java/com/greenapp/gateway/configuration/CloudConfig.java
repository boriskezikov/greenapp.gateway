package com.greenapp.gateway.configuration;

import com.greenapp.gateway.utils.AuthUtilsService;
import com.greenapp.gateway.utils.UserAuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class CloudConfig {

    @Value("${gateway.auth.uri}")
    private String AUTH_URL;

    @Value("${gateway.task.uri}")
    private String TASK_PROVIDER_URL;

    private final AuthUtilsService service;

    private static final Logger LOG = Logger.getLogger(CloudConfig.class.getName());

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        System.out.println("________________________________________________________________________________________________________________________");
        System.out.println("URLS TO REDIRECT: " + AUTH_URL);
        System.out.println("________________________________________________________________________________________________________________________");
        return builder.routes()
                .route(r -> r
                        .path("/auth/**")
                        .filters(defaultFilters())
                        .uri(AUTH_URL)
                        .id("authModule"))
                .route(r -> r
                        .path("/task-provider/**")
                        .filters(defaultFilters())
                        .uri(TASK_PROVIDER_URL)
                        .id("taskProviderModule"))
                .build();
    }

    private Function<GatewayFilterSpec, UriSpec> defaultFilters() {
        return f -> f.preserveHostHeader().filter(this::addHeaders);
    }

    private Mono<Void> addHeaders(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-GREEN-APP-ID", "GREEN")
                .build();
        if (service.validateAuthentication(exchange)) {
            LOG.info(">> Authenticated: " + service.generateBasicAuth());
            LOG.info("-------------------------------------------------------------------------------------------");
            LOG.info(String.format(">> Begin request: %s", request.getURI()));
            LOG.info(String.format(">> Headers: %s", request.getHeaders()));
            LOG.info(String.format(">> Cookies: %s", request.getCookies()));

            if (request.getURI().toString().contains("auth")) {
                LOG.warning(String.format(">> Redirecting to : %s", AUTH_URL));
            } else if (request.getURI().toString().contains("task-provider")) {
                LOG.warning(String.format(">> Redirecting to : %s", TASK_PROVIDER_URL));
            }
            LOG.info(String.format(">> End request: %s", request.getId()));
            LOG.info("-------------------------------------------------------------------------------------------");

            exchange.mutate()
                    .request(request)
                    .build();

            return chain.filter(exchange);
        }
        LOG.warning(">> Captured request with invalid authentication credentials!");
        return Mono.error(new UserAuthorizationException("Incorrect basic auth credentials"));
    }
}