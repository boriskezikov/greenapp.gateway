package com.greenapp.gateway.utils;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Service unavailable")
public class ServiceUnavailableException extends HystrixBadRequestException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
