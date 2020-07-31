package com.spring.security.oauth2.demo.model;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserDTO {

    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 姓名
     */
    private String name;




}
