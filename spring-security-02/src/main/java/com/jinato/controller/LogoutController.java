package com.jinato.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LogoutController {
    @RequestMapping("/logout.html")
    public String logout(){
        return "logout";
    }
}
