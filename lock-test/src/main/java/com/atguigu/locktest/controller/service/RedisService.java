package com.atguigu.locktest.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * +1
     */
    public void incr() {

        String num = redisTemplate.opsForValue().get("num");
        Integer i = Integer.parseInt(num);
        i++;

        redisTemplate.opsForValue().set("num",i.toString());


    }
}
