package com.atguigu.gulimall.pms.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient  // 开启nacos服务注册发现功能
@EnableFeignClients(basePackages = "com.atguigu.gulimall.pms.feign")     // 开启远程调用,并指定feign的包
public class PmsCloudConfig {
}
