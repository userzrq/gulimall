package com.atguigu.gulimall.gateway.filter;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 全局令牌认证过滤器
 * <p>
 * 系统JWT认证过滤器，只有token校验通过才能请求接口
 * <p>
 * 全局过滤器或者 gateway filterfactory 在容器中添加都是有顺序的
 * 如果过滤器顺序太低，过滤器是不会被执行的
 * 数字越小，优先级越高
 * <p>
 * redis的自动续期
 */
@Slf4j
@Component("guliAuthenticationFilter")
@Order(1)
public class GuliAuthenticationFilter implements GlobalFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Webflux编程方式，流式编程
     *
     * @param exchange
     * @param chain
     * @return mvc中的过滤器：doFilter(req,resp,filterChain);
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 判断请求头中是否携带了授权字段 Authentication,如果携带就对令牌进行校验
        ServerHttpRequest request = exchange.getRequest();
        List<String> authentication = request.getHeaders().get("Authentication");
        if (authentication != null && authentication.size() > 0) {
            String token = authentication.get(0);
            log.info("获得的authentication验证请求头:{}", authentication.get(0));
            try {
                // 获取令牌和验证 载荷 内的内容，二合一方法
                // 在将请求放过去后，对redis中保存的用户信息进行自动续期
                Map<String, Object> jwtBody = GuliJwtUtils.getJwtBody(token);


                String suffix = (String) jwtBody.get("token");
                String redisKey = Constant.LOGIN_USER_PREFIX + suffix;

                // redis过期时间续期,自动覆盖
                redisTemplate.expire(redisKey, Constant.LOGIN_USER_TIMEOUT_MINUTES, TimeUnit.MINUTES);

                Mono<Void> filter = chain.filter(exchange);
                return filter;
            } catch (Exception e) {
                // 检查失败
                ServerHttpResponse response = exchange.getResponse();
                // 设置响应403状态码，
                response.setStatusCode(HttpStatus.FORBIDDEN);

                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }

        }

        log.info("网关全局令牌验证结束......");

        // 请求头 Authentication 中的 Bearer 是一种规范
        // JWT Oauth2 其他token 如果都是用Authentication这个Header , Bearer或者其他字段可以作为一个区分的标识，区分是JWT 还是其他认证方式

        // Authorization <type> <credentials>   Basic Bearer Digest Oauth....

        // exchange.getResponse();


        // 其他不需要带令牌的请求就放行
        return chain.filter(exchange);
    }
}
