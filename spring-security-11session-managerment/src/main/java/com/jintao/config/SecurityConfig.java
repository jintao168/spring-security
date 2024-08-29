package com.jintao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //操作session的对象
    @Autowired
    private FindByIndexNameSessionRepository findByIndexNameSessionRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .csrf().disable()
                .sessionManagement()//开启会话管理
                .maximumSessions(1)//允许会发最大并发数，这里只能允许一个客户端
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/login")
                .sessionRegistry(sessionRegistry());
    }

//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher(){
//        return new HttpSessionEventPublisher();
//    }
    @Bean
    public SpringSessionBackedSessionRegistry sessionRegistry(){
        return new SpringSessionBackedSessionRegistry(findByIndexNameSessionRepository);
    }
}
