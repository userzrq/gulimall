package com.atguigu.gulimall.pms.component;

import com.atguigu.gulimall.pms.annotation.GuliCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.reflect.Method;

/**
 * 切面的步骤
 * 0.导入aop starter
 * 1.@Aspect注解，声明这是一个切面
 * 2.@Component，加入容器中
 * 3.定义切入点
 */
@Slf4j
@Component
@Aspect
public class GuliCacheAspect {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 环绕通知：方法执行前，结束后，出现异常，正常返回都能通知
     *
     * @param joinPoint
     * @return
     */
    @Around("@annotation(com.atguigu.gulimall.pms.annotation.GuliCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {


        Object result = null;
        try {
            log.info("缓存切面介入工作...前置通知");
            Object[] args = joinPoint.getArgs(); //获取目标方法的所有参数值

            // 拿到注解的值
            // 方法一
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            GuliCache guliCache = signature.getMethod().getAnnotation(GuliCache.class);
            String prefix = guliCache.prefix();
            log.info("获取到的注解的prefix值：{}" + prefix);

            // 方法二
            String name = joinPoint.getSignature().getName();
            for (Method method : joinPoint.getThis().getClass().getMethods()) {
                if (method.getName().equals(name)) {
                    GuliCache mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, GuliCache.class);
                    log.info("获取到的注解的prefix值：{}" + mergedAnnotation);
                }
            }


            //redisTemplate.opsForValue().get();

            // 目标方法真正执行
            result = joinPoint.proceed();
            log.info("缓存切面介入工作...方法执行完成,返回通知");
        } catch (Exception e) {
            log.info("缓存切面介入工作...异常通知");
        } finally {
            log.info("缓存切面介入工作...后置通知");
        }

        return result;
    }
}
