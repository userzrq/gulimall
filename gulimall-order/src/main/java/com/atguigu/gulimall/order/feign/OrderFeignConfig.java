package com.atguigu.gulimall.order.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
@Configuration
public class OrderFeignConfig {

    /**
     * RequestInterceptor
     * template.header("Authorization", headerValue);
     * this.headerValue = "Basic " + base64Encode((username + ":" + password).getBytes(charset));
     * headValue 有被赋值 但是prefix"Basic"与我们定义的 Bearer不同
     *
     *
     * 当线程调用了feign服务时，拦截器对远程调用服务的线程进行拦截
     *
     * @return
     */
    @Bean("authorizationRequestInterceptor")
    @Primary
    public RequestInterceptor authorizationRequestInterceptor() {

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                log.info("拦截器的线程号:{}", Thread.currentThread().getId());
                // 目的是获取进来调用feign的 主线程 的requst对象中的 请求头属性
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                HttpServletRequest request = attributes.getRequest();
                String authorization = request.getHeader("Authorization");
                template.header("Authorization", authorization);
            }
        };
    }


    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();

                if (headerNames != null) {
                    // 遍历header中的name和value
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String values = request.getHeader(name);
                        requestTemplate.header(name, values);
                    }
                }
            }
        };
    }

}
