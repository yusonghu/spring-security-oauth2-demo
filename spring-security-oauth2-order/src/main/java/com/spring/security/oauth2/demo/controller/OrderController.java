package com.spring.security.oauth2.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/select")
    @PreAuthorize("hasAuthority('select')")
    public String select(){
        return "查询资源";
    }

    @GetMapping("/other")
    @PreAuthorize("hasAuthority('other')")
    public String other(){
        return "其它资源";
    }
}
