package com.greenapp.gateway.services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/live")
public class KeepAliveController {

    @GetMapping
    public void live(){
        System.out.println("Live request accepted");
    }
}
