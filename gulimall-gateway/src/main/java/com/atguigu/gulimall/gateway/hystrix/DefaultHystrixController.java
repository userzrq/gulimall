package com.atguigu.gulimall.gateway.hystrix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
public class DefaultHystrixController {


    @RequestMapping("/defaultfallback")
    public Map<String, String> defaultfallback() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        String dateNowStr = LocalDateTime.now().format(formatter);

        log.info("DefaultHystrixController降级操作...defaultfallback");
        Map<String, String> map = new HashMap<>();
        map.put("resultCode", "fail");
        map.put("resultMessage", "服务繁忙，请稍后重试defaultfallback");
        map.put("time", dateNowStr);
        return map;

    }


    @RequestMapping("/defaultfallback1")
    public Map<String, String> defaultfallback1() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        String dateNowStr = LocalDateTime.now().format(formatter);

        log.info("DefaultHystrixController降级操作...defaultfallback1");
        Map<String, String> map = new HashMap<>();
        map.put("resultCode", "fail");
        map.put("resultMessage", "服务繁忙，请稍后重试defaultfallback1");
        map.put("time", dateNowStr);
        return map;
    }


    @RequestMapping("/defaultfallback2")
    public Map<String, String> defaultfallback2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        String dateNowStr = LocalDateTime.now().format(formatter);

        log.info("DefaultHystrixController降级操作...defaultfallback2");
        Map<String, String> map = new HashMap<>();
        map.put("resultCode", "fail");
        map.put("resultMessage", "服务繁忙，请稍后重试defaultfallback2");
        map.put("time", dateNowStr);
        return map;
    }
}
