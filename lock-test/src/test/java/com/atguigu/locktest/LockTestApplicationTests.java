package com.atguigu.locktest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class LockTestApplicationTests {

    Map<String, Object> map = new HashMap<>();

    @Test
    void contextLoads() {
            String st1 = "a" + "b" + "c";
            String st2 = "abc";
            System.out.println(st1 == st2);
            System.out.println(st1.equals(st2));

    }

}



