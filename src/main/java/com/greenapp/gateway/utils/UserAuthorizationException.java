package com.greenapp.gateway.utils;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid auth credentials")
public class UserAuthorizationException extends HystrixBadRequestException {

    public UserAuthorizationException(String message) {
        super(message);
    }
}
