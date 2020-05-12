package com.atguigu.gulimall.oms.controller;

import com.atguigu.gulimall.oms.feign.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class HelloController {
    @Autowired
    private WorldService worldService;

    @Value("${my.content}") //从配置文件中读取名为my.content的值
    private String content = "";

    @Value("${redis.url}")
    private String redisUrl = "";

    @Value("${spring.datasource.url}")
    private String datasourceUrl = "";


    @GetMapping(value = "hello")
    public String Hello(){
        return "hello " + worldService.world(); //+ content +redisUrl +datasourceUrl;
    }
}
