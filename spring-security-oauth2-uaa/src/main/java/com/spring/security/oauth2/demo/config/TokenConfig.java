package com.spring.security.oauth2.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class TokenConfig {

    //  令牌存储策略
//    @Bean
//    public TokenStore tokenStore(){
//        //  基于内存的token
//        return new InMemoryTokenStore();
//    }

    private String SINGING_KEY = "SIGIN_UAA";

    @Bean
    public TokenStore tokenStore() {
        //  JWT 令牌存储方案
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SINGING_KEY);       //  对称密钥，资源服务器使用该密钥来验证
        return converter;
    }

}
