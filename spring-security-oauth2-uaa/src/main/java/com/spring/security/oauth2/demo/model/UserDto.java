package com.spring.security.oauth2.demo.model;

import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 **/
@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String mobile;
}
