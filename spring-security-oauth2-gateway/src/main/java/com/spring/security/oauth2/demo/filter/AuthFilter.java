package com.spring.security.oauth2.demo.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        //  获取当前用户身份信息
        String principal = oAuth2Authentication.getName();
        //  获取当前用户权限信息
        List<String> authorities = new ArrayList<>();
        oAuth2Authentication.getAuthorities().stream().forEach(v -> authorities.add(v.getAuthority()));
        //  把身份和权限信息放在json中，加入http的header中
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
        Map<String,Object> jsonToken = new HashMap<>(requestParameters);
        if (oAuth2Authentication != null) {
            jsonToken.put("principal",principal);
            jsonToken.put("authorities",authorities);
        }
        currentContext.addZuulRequestHeader("json-token", Base64.encode(JSON.toJSONString(jsonToken)));
        //  转发给微服务
        return null;
    }
}
