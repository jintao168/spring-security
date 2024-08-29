package com.jintao.security.filter;

import com.jintao.exception.KaptchaNotMatchException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//自定义验证码 filter
public class KaptchaFilter extends UsernamePasswordAuthenticationFilter {

    public static final String FORM_KAPTCHA_KEY = "kaptcha";

    private String kaptchaParameter = FORM_KAPTCHA_KEY;

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
        //从请求中获取验证码
        //与session中验证码进行比较
        String verifyCode = request.getParameter(getKaptchaParameter());//这样写就写死了，可以仿照uname，passwd灵活
        String code = (String) request.getSession().getAttribute("code");
        if (!ObjectUtils.isEmpty(verifyCode) && !ObjectUtils.isEmpty(code) && verifyCode.equalsIgnoreCase(code)) {
            return super.attemptAuthentication(request, response);
        }
        throw new KaptchaNotMatchException("验证码不匹配！");
    }
}
