package com.atguigu.gulimall.gateway.test;

import com.atguigu.gulimall.commons.utils.GuliJwtUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtTest {

    public static void main(String[] args) {

        Map<String, Object> payload = new HashMap();
        String token = UUID.randomUUID().toString().replace("-", "").substring(1, 5);
        System.out.println(token);
        payload.put("token", token);
        payload.put("userId", 3);

        String jwttoken = GuliJwtUtils.buildJwt(payload, null);

        System.out.println(jwttoken);

        // eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjMsInRva2VuIjoiZDg3ZiJ9._JGCO5Gdp2dOkXbqSZTdhvTm-ka2gxF73HYqCJ6o62I
        // eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjMsInRva2VuIjoiNDA2ZSJ9.uo0JM8kStzkbSRd40joa-d42PMvfvcBGqSc5URC2h58
    }
}
