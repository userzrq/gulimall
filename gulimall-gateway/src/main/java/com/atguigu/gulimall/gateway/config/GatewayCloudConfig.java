package com.atguigu.gulimall.gateway.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient  //开启nacos服务注册发现功能
public class GatewayCloudConfig {
}
