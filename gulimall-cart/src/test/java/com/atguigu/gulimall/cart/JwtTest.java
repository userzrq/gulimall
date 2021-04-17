package com.atguigu.gulimall.cart;

import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhangruiqi 10017
 * @create 2021/3/10
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class JwtTest {

    @Test
    public void GeneJwtTest() {
        Map<String, Object> payload = new HashMap<>();

        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 5);

        payload.put("token", token);
        payload.put("userId", "2");
        String jwt = GuliJwtUtils.buildJwt(payload, null);

        System.out.println(jwt);
    }


}
