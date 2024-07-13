package com.jintao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jintao.filter.security.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.HashMap;
import java.util.UUID;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.withUsername("admin").password("{noop}admin").roles("admin").build());
        return inMemoryUserDetailsManager;
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setUsernameParameter("uname");
        loginFilter.setPasswordParameter("passwd");
        loginFilter.setRememberMeParam("remember");
        loginFilter.setFilterProcessesUrl("/doLogin");
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        //设置认证成功时使用自定义rememberMeService
        //这个是第一次，认证成功后会写入
        loginFilter.setRememberMeServices(rememberMeServices());
        loginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("msg", "登录成功");
            map.put("status", HttpStatus.OK.value());
            map.put("userInfo", authentication.getPrincipal());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(map));
        });
        loginFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("msg", "登录失败");
            map.put("status", HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(map));
        });
        return loginFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .rememberMe()//开启记住我
                //设置自动登录  使用那个rememberMeService
                //但是也要保证认证成功时也用rememberMeService，如何做？
                //这里的rememberMeServices是第二次，会话过期是认证，两个地方缺一不可
                .rememberMeServices(rememberMeServices())
                .and()
                //传统web开发未认证情况下访问受保护资源跳转到登录页面
                //而前后端不需要，直接返回异常信息
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().print("请认证之后再去处理！");
                })
                .and()
                .logout()
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/logout"),
                        new AntPathRequestMatcher("/logout1")
                ))
                .logoutSuccessHandler((request, response, authentication) -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("msg", "注销成功");
                    map.put("status", 200);
                    map.put("用户信息", authentication.getPrincipal());
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.OK.value());
                    ObjectMapper om = new ObjectMapper();
                    response.getWriter().print(om.writeValueAsString(map));
                })
                .and()
                .csrf().disable();

        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);

    }
    @Bean
    public RememberMeServices rememberMeServices(){
        return new MyRememberMeService(UUID.randomUUID().toString(), userDetailsService(),new InMemoryTokenRepositoryImpl());
    }
}
