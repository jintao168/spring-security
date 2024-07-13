package com.jintao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jintao.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import java.util.HashMap;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Bean
//    public UserDetailsService userDetailsService(){
//        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//        inMemoryUserDetailsManager.createUser(User.withUsername("root").password("{noop}123").roles("admin").build());
//        return inMemoryUserDetailsManager;
//    }
    @Autowired
    private MyUserDetailService userDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    //暴露出来AuthenticationManager

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setFilterProcessesUrl("/doLogin");//指定认证url
        loginFilter.setUsernameParameter("uname"); //指定接收Json 用户名 key
        loginFilter.setPasswordParameter("passwd");//指定接受Json 密码 key
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("msg", "登录成功");
            map.put("status", 200);
            map.put("用户信息", authentication.getPrincipal());//强转为User才能拿到详细信息
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().print(objectMapper.writeValueAsString(map));
        });//认证成功处理
        loginFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("msg", "用户名密码错误");
            map.put("status", 500);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ObjectMapper om = new ObjectMapper();
            response.getWriter().print(om.writeValueAsString(map));
        });//认证失败处理
        return loginFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()//还是得formlogin，但是底层得变化，因为传过来参数是json格式，不能默认从request中直接获取
                .and()
                .exceptionHandling()//异常处理，前后端分离我只返回JSON数据，当你访问资源未认证，我要返回json数据，而不是给我一个页面
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().print("请认证之后再去处理！");
                })//异常处理又分为认证异常和授权异常,这个是认证异常
                .and()
                .logout()
//                .logoutUrl("/logout")
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/logout", HttpMethod.GET.name()),
                        new AntPathRequestMatcher("/logout1",HttpMethod.DELETE.name())
                ))
                .logoutSuccessHandler((request, response, authentication) -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("msg", "注销成功");
                    map.put("status", 200);
                    map.put("用户信息",authentication.getPrincipal());
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.OK.value());
                    ObjectMapper om = new ObjectMapper();
                    response.getWriter().print(om.writeValueAsString(map));
                })
                .and()
                .csrf().disable();
        //重写下UsernameAuthenticationFilter
        //at:用某个filter替换过滤器中哪个filter before:放在过滤器链中哪个filter之前 after:
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
