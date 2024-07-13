package com.jintao.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jintao.exception.VerifyCodeNotMatchException;
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

public class LoginKaptchaFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SPRING_SECURITY_KAPTCHA_KEY = "kaptcha";
    private String kaptchaParameter = SPRING_SECURITY_KAPTCHA_KEY;

    public String getKaptchaParameter() {
        return kaptchaParameter;
    }

    public void setKaptchaParameter(String kaptchaParameter) {
        this.kaptchaParameter = kaptchaParameter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //获取请求中验证码,这里请求中是json数据，注意
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE) || request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
            try {
                //获取请求数据
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> userInfo = objectMapper.readValue(request.getInputStream(), Map.class);
                String kaptchaCode = userInfo.get(getKaptchaParameter());
                String username = userInfo.get(getUsernameParameter());
                String password = userInfo.get(getPasswordParameter());
                //获取session验证码
                String kaptcha = (String) request.getSession().getAttribute("kaptcha");
                if (!ObjectUtils.isEmpty(kaptchaCode) && !ObjectUtils.isEmpty(kaptcha) && kaptchaCode.equalsIgnoreCase(kaptcha)) {
                    //获取用户名和和密码认证
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
                    setDetails(request, authenticationToken);
                    return getAuthenticationManager().authenticate(authenticationToken);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new VerifyCodeNotMatchException("验证码不正确");
        }
        return super.attemptAuthentication(request, response);
    }
}
