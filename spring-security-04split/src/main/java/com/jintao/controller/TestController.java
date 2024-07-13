package com.jintao.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String text(){
        System.out.println("test ...");
        return "test ok";
    }
}
