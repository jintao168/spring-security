package com.jinato.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        System.out.println("hello");
        //获取认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("身份信息:"+authentication.getPrincipal());//其实就是个User对象，可以强转
        User principal = (User) authentication.getPrincipal();
        System.out.println("权限信息："+authentication.getAuthorities());//权限信息可以在yml中配置
//        authentication.getCredentials() 凭证信息是拿不到的，登录成功后是受保护的，已经擦除掉了，设为null了
        new Thread(()->{
            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("子线程身份信息："+authentication1);//子线程拿到了也是空
        }).start();
        return "hello";
    }
}
