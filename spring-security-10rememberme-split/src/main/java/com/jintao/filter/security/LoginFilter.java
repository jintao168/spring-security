package com.jintao.filter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    public static final String REMEMBER_ME_PARAM = "remember";

    public String rememberMeParam = REMEMBER_ME_PARAM;

    public String getRememberMeParam() {
        return rememberMeParam;
    }

    public void setRememberMeParam(String rememberMeParam) {
        this.rememberMeParam = rememberMeParam;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (request.getContentType().equals(MediaType.APPLICATION_JSON)||request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)){
            try {
                Map<String, String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String username = userInfo.get(getUsernameParameter());
                String password = userInfo.get(getPasswordParameter());
                String rememberMeValue = userInfo.get(getRememberMeParam());
                if (!ObjectUtils.isEmpty(rememberMeValue)) {
                    request.setAttribute(getRememberMeParam(), rememberMeValue);
                }
                System.out.println("用户名：" + username + " 密码：" + password);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,password);
                this.setDetails(request, usernamePasswordAuthenticationToken);
                return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return super.attemptAuthentication(request, response);
    }
}
