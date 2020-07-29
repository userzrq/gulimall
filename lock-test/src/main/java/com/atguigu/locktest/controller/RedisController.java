package com.atguigu.locktest.controller;

import com.atguigu.locktest.controller.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/incr")
    public String incr() {
        redisService.incr();

        return "ok";
    }
}
