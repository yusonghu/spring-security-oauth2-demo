package com.spring.security.oauth2.demo.config;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 这里鉴权与单体的一样需要进行三相配置
 */
@Configuration
@EnableAuthorizationServer
public class Authorization extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;



    //  令牌访问安全策略
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
        security.tokenKeyAccess("permitAll()")             //   /oauth/token_key        公开
                .checkTokenAccess("permitAll()")           //   /oauth/check_token      公开
                .allowFormAuthenticationForClients();      //   允许表单提交
    }

    //  将客户端信息从数据库中来
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        super.configure(clients);
        //  使用内存的方式
//        clients.inMemory()          //  使用内存的方式
//                .withClient("phone")       //  客户端id
//                .secret(new BCryptPasswordEncoder().encode("secret"))               //  客户端密钥
//                .resourceIds("res1")             //  资源列表
//                .authorizedGrantTypes("authorization_code", "password","client_credentials","implicit","refresh_token")// 该client允许的授权类型authorization_code,password,refresh_token,implicit,client_credentials
//                .scopes("all")                  //  授权允许范围
//                .autoApprove(false)             //  false 跳转到授权页面   true 直接发令牌
//                .redirectUris("http://www.baidu.com");               //验证回调地址

        //  使用数据库的方式
        clients.withClientDetails(clientDetailsService);
    }

    @Autowired
    private ClientDetailsService clientDetailsService;

    //  令牌访问服务
    @Bean
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        defaultTokenServices.setSupportRefreshToken(true);      //    是否产生刷新令牌
        defaultTokenServices.setAccessTokenValiditySeconds(7200);   //      令牌有效期默认两小时
        defaultTokenServices.setRefreshTokenValiditySeconds(2592000);       //  刷新令牌默认有效期3天
        defaultTokenServices.setTokenStore(tokenStore);     //  令牌存储策略
        //  设置令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter));
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
        return defaultTokenServices;
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    //  令牌访问端点
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
        endpoints
                .authenticationManager(authenticationManager)              //  密码模式
                .authorizationCodeServices(authorizationCodeServices)       //  授权码模式
                .tokenServices(tokenServices())                             //  令牌管理服务
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);        //  允许POST请求
    }
}
