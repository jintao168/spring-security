package com.jintao.config;

import com.jintao.security.filter.KaptchaFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public KaptchaFilter kaptchaFilter() throws Exception {
        //现在整个认证都是由他处理了,所以要在这里配置他的认证请求了
        KaptchaFilter kaptchaFilter = new KaptchaFilter();
        kaptchaFilter.setFilterProcessesUrl("/doLogin");
        kaptchaFilter.setUsernameParameter("uname");
        kaptchaFilter.setPasswordParameter("passwd");
        kaptchaFilter.setKaptchaParameter("kaptcha");
        //完成认证，必须要知道认证管理器是谁
        kaptchaFilter.setAuthenticationManager(authenticationManagerBean());
        //指定认证成功处理
        kaptchaFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            response.sendRedirect("/index.html");
        });
        //指定认证失败处理
        kaptchaFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            response.sendRedirect("/login.html");
        });
        return kaptchaFilter;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.withUsername("admin").password("{noop}admin").roles("admin").build());
        return inMemoryUserDetailsManager;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/login.html").permitAll()
                .mvcMatchers("/vc.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
//                .loginProcessingUrl("/doLogin")//这里都是UsernamePasswordAuthenticationFilter默认实现
//                .usernameParameter("uname")//这里都是UsernamePasswordAuthenticationFilter默认实现
//                .passwordParameter("passwd")//这里都是UsernamePasswordAuthenticationFilter默认实现
//                .defaultSuccessUrl("/index.html")//这里都是UsernamePasswordAuthenticationFilter默认实现
//                .failureUrl("/login.html")//这里都是UsernamePasswordAuthenticationFilter默认实现
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html")
                .and()
                .csrf().disable();

        http.addFilterAt(kaptchaFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}