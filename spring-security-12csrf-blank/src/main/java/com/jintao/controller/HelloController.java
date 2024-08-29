package com.jintao.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @PostMapping("/withdraw")
    public String withDraw() {
        System.out.println("执行一次转账操作");
        return "执行一次转账操作";
    }
}
