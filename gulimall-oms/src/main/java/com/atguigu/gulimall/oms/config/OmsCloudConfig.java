package com.atguigu.gulimall.oms.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient  //开启服务发现功能
@EnableFeignClients(basePackages = "com.atguigu.gulimall.oms.feign")    //配置调用接口所在的包开启远程服务调用功能
public class OmsCloudConfig {
}
