package com.atguigu.gulimall.gateway.hystrix;

import com.atguigu.gulimall.commons.bean.Resp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HystrixController {

    @GetMapping("/incaseoffailureusethis")
    public Resp<Object> incaseoffailureusethis(){

        return Resp.fail("远程服务调用失败");
    }
}
