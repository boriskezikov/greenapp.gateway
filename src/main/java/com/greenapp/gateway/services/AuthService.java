package com.greenapp.gateway.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.greenapp.gateway.utils.Constants.AUTHENTICATION;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${zuul.routes.auth.url}")
    private String AUTH_URL;

    private final RestTemplate restTemplate;

    public Boolean authenticate(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-GREEN-APP-ID", "GREEN");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(AUTH_URL + AUTHENTICATION)
                .queryParam("token", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                Boolean.class);
        return response.getBody();
    }
}
