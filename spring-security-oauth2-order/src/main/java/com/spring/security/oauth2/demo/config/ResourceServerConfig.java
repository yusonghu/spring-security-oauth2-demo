package com.spring.security.oauth2.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    //  资源ID        (这个得与授权服务得客户端得ResourceId 一致)
    public static final String RESOURCE_ID = "res1";


    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
        resources.resourceId(RESOURCE_ID)   //资源ID

//                .tokenServices(tokenService())        //  远程验证令牌服务
                .tokenStore(tokenStore)                 //  本地验证令牌服务
                .stateless(true);           //  无状态
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests().antMatchers("/**")
                .access("#oauth2.hasAnyScope('ROLE_API')")                                   //  这个scope是授权服务器得授的授权范围
                .and().csrf().disable()                                                          //  禁用csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);    //  无状态
    }


    //  远程验证token
//    @Bean
    public ResourceServerTokenServices tokenService() {
        //  远程服务请求授权服务器校验token，必须指定校验token、client_id、client_secret
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:53020/uaa/oauth/check_token");
        remoteTokenServices.setClientId("phone");
        remoteTokenServices.setClientSecret("secret");
        return remoteTokenServices;
    }
}
