package com.jintao.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
public class DemoController {
    @GetMapping("/admin") //必须具有ADMIN这个角色才可以访问
    public String admin() {
        return "admin ok";
    }

    @GetMapping("/user") //必须具有USER这个角色才可以访问
    public String user() {
        return "user ok";
    }

    @GetMapping("/getInfo") //必须具有READ_INFO这个权限才可以访问
    public String getInfo(){
        return "info ok";
    }
}
