package com.spring.security.oauth2.demo.model;

import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 **/
@Data
public class PermissionDto {

    private Long id;
    private String code;
    private String description;
    private String url;
}
