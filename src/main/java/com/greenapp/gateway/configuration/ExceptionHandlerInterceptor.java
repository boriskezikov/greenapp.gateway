package com.greenapp.gateway.configuration;

import com.greenapp.gateway.utils.ServiceUnavailableException;
import com.netflix.zuul.exception.ZuulException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerInterceptor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ZuulException.class)
    protected ResponseEntity<Object> handle(Exception ex, WebRequest request) {
        throw new ServiceUnavailableException("Sorry, heroku issues:)");
    }

}
