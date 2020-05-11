package com.atguigu.gulimall.oms.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * nacos远程接口调用示例
 */
@FeignClient(name = "gulimall-pms")
public interface WorldService {

    @GetMapping("/world")
    public String world();
}
