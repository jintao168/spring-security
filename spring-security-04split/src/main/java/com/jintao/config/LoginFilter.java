package com.jintao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 自定义前后端分离认证 Filter
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //判断是否为POST请求,类型这里也可以自定义
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //判断是否是json格式请求类型
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)||request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)){
            //从json数据中获取用户输入用户名和密码进行认证
            try {
                Map<String,String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String username = userInfo.get(getUsernameParameter());
                String password = userInfo.get(getPasswordParameter());
                System.out.println("用户名："+username+" 密码："+password);

                //仿照着源码写
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,password);
                setDetails(request,authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //从json数据中获取用户输入用户名和密码进行认证
        return super.attemptAuthentication(request, response);
    }
}
