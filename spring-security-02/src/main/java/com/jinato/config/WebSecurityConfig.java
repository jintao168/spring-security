package com.jinato.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final MyUserDetailService userDetailService;

    @Autowired
    public WebSecurityConfig(MyUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        System.out.println("自定义AuthenticationManager：" + builder);
        builder.userDetailsService(userDetailService);//其实就是默认的DaoAuthenticationProvider
        //还可以追加各种认证方式
//        builder.authenticationProvider(new ..)
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/login.html").permitAll()
                .mvcMatchers("/index").permitAll()//放行资源需要写在前面
                .anyRequest().authenticated()
                .and()
                .formLogin()//form表单认证
                .loginPage("/login.html")//用来指定默认的登录页面 注意：一旦自定义登录页面以后只能登录url
                .loginProcessingUrl("/doLogin")//指定处理登录请求url
                .usernameParameter("uname")
                .passwordParameter("passwd")
//                .successForwardUrl("/hello")// 认证成功 跳转路径 forward 始终在认证成功后跳转到指定请求
//                .defaultSuccessUrl("/hello")//认证成功 redirect 根据上一保存请求进行成功跳转
                .successHandler(new MyAuthenticationSuccessHandler())//认证成功时处理 前后端分离开发方案
//                .failureForwardUrl("/login.html")//认证失败后 forward 跳转
//                .failureUrl("/login.html")//默认认证失败后 redirect 跳转
                .failureHandler(new MyAuthenticationFailureHandler())
                .and()
                .logout()
//                .logoutUrl("/logoutaa")//默认地址是/logout url默认请求方式必须是GET
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/aa", "GET"),
                        new AntPathRequestMatcher("/bb","POST")
                ))
                .invalidateHttpSession(true)//默认 会话失效
                .clearAuthentication(true)// 默认 清除认证标记
//                .logoutSuccessUrl("/login.html") //注销登录成功跳转的页面 也是默认配置 传统开发方式
                .logoutSuccessHandler(new MyLogoutSuccessHandler())
                .and()
                .csrf().disable();//禁止 csrf 跨站请求保护

    }
}
