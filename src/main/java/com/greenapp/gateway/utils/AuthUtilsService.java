package com.greenapp.gateway.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.Objects;

@Service
public class AuthUtilsService {

    @Value("${username}")
    private String user;

    @Value("${password}")
    private String password;


    public boolean validateAuthentication(ServerWebExchange serverWebExchange) {
        var authHeader = Objects.requireNonNull(serverWebExchange.getRequest()
                .getHeaders()
                .get(HttpHeaders.AUTHORIZATION))
                .stream()
                .findAny();

        return authHeader.isPresent() && authHeader.get().equals(generateBasicAuth());


    }

    public String generateBasicAuth() {
        return "Basic " + new String(Base64.encodeBase64((user + ":" + password).getBytes()));
    }
}
