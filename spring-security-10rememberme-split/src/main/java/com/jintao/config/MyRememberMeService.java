package com.jintao.config;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;

//自定义记住我 service实现类
public class MyRememberMeService extends PersistentTokenBasedRememberMeServices {
    public MyRememberMeService(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    /**
     * 自定义前后端分离获取 remember-me 方式
     * @param request
     * @param parameter
     * @return
     */
    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        String rememberMeValue = (String) request.getAttribute(parameter);
        if (rememberMeValue == null || !rememberMeValue.equalsIgnoreCase("true") && !rememberMeValue.equalsIgnoreCase("on") && !rememberMeValue.equalsIgnoreCase("yes") && !rememberMeValue.equals("1")) {
            this.logger.debug(LogMessage.format("Did not send remember-me cookie (principal did not set parameter '%s')", parameter));
            return false;
        } else {
            return true;
        }
    }
}
