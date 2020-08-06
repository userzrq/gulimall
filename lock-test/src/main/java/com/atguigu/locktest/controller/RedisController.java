package com.atguigu.locktest.controller;

import com.atguigu.locktest.controller.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/incr")
    public String incr() {
        redisService.incr();

        return "ok";
    }

    @GetMapping("/read")
    public String readValue() throws InterruptedException {
        return redisService.read();
    }

    @GetMapping("/write")
    public String writeValue() throws InterruptedException {
        return redisService.write();
    }

    @GetMapping("/lockdoor")
    public String lockdoor() throws InterruptedException {
        // redis成功把门锁上,就return ok
        redisService.lockdoor();
        return "ok";
    }

    @GetMapping("/gobackhome")
    public void gobackhome(){

        redisService.gobackhome();
    }

}
