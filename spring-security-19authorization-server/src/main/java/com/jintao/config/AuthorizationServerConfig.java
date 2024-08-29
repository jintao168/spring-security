package com.jintao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

//自定义 授权服务器配置
@Configuration
@EnableAuthorizationServer//指定当前应用为授权服务器
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;

    //配置授权服务器使用哪个 userDetailService
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.userDetailsService(userDetailsService);
    }

    // 用来配置授权服务器可以为哪些客户端授权 id secret redirectURI 使用那种授权模式
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client")
                .secret(passwordEncoder.encode("security"))// 注册客户端密钥，要求不能明文
                .redirectUris("http://www.baidu.com")
                //既支持授权码模式，同时也支持颁发的令牌刷新模式
                .authorizedGrantTypes("authorization_code","refresh_token")//授权服务器支持的模式，这里意思是仅支持授权码模式
                .scopes("read:user");//令牌允许获取的资源权限
    }
    //授权码模式：1 请求用户是否授权 /oauth/authorize
    //2授权后根据获取的授权码获取令牌 /oauth/token id secret redirectUri code
    //

}
