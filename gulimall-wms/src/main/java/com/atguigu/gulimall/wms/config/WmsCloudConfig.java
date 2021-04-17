package com.atguigu.gulimall.wms.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("com.atguigu.gulimall.wms.feign")
@EnableDiscoveryClient  //开启nacos服务注册发现功能
public class WmsCloudConfig {
}
