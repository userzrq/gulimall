package com.atguigu.gulimall.gateway.config;

import com.atguigu.gulimall.gateway.filter.GuliAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GulimallGatewayConfig {

    /**
     * 跨域过滤器
     * Gateway
     *  Reactive;  Webflux;
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter(){

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);    //允许携带cookie的跨域请求

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);     //注册跨域

        return new CorsWebFilter(source);
    }


//    @Bean
//    @Order(1)
//    public GuliAuthenticationFilter authenticationFilter(){
//        return new GuliAuthenticationFilter();
//    }
}
