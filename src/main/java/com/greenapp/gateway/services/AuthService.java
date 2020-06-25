package com.greenapp.gateway.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.greenapp.gateway.utils.Constants.AUTHENTICATION;
import static com.greenapp.gateway.utils.Constants.CLIENT_INFO;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${zuul.routes.auth.url}")
    private String AUTH_URL;

    private final RestTemplate restTemplate;


    public Long getClient(String mail){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(AUTH_URL + CLIENT_INFO)
                .queryParam("mail", mail);

        HttpEntity<?> entity = new HttpEntity<>(provideHeaders());

        ResponseEntity<Long> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                Long.class);
        return response.getBody();
    }

    public UserInfo authenticate(String username) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(AUTH_URL + AUTHENTICATION)
                .queryParam("mail", username);

        HttpEntity<?> entity = new HttpEntity<>(provideHeaders());

        ResponseEntity<UserInfo> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                UserInfo.class);
        return response.getBody();
    }
//    @Scheduled(cron = "* */15 * * * *")
//    public void keepMailAlivePlease() {
//
//        restTemplate.exchange(
//                "https://greenapp-mail-service.herokuapp.com/leave",
//                HttpMethod.GET,
//                null,
//                Void.class
//        );
//        System.out.println("Sent live");
//    }

    private HttpHeaders provideHeaders(){
        var headers = new HttpHeaders();
        headers.set("X-GREEN-APP-ID", "GREEN");
        return headers;
    }
}
