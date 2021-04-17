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
import java.util.Objects;

/**
 * 远程调用服务的请求拦截器
 * 在发送请求前会发送的模板进行操作
 *
 * @author 10017
 */
@Slf4j
@Configuration
public class OrderFeignConfig {

    /**
     * RequestInterceptor
     * template.header("Authorization", headerValue);
     * this.headerValue = "Basic " + base64Encode((username + ":" + password).getBytes(charset));
     * headValue 有被赋值 但是prefix"Basic"与我们定义的 Bearer不同
     * <p>
     * <p>
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
                // 获取异步线程中的ServletRequestAttributes对象（从主线程中复制而来的）
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                // 消息队列消费者中的调用没有走请求，是直接在队列中进行调用的，attributes对象为空，需要判空
                if (Objects.isNull(attributes)) {

                } else {
                    // 对请求发送的模板进行操作
                    HttpServletRequest request = attributes.getRequest();
                    String authorization = request.getHeader("Authorization");
                    template.header("Authorization", authorization);
                }
            }
        };
    }
}
