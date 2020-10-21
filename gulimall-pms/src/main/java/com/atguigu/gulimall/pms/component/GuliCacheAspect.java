package com.atguigu.gulimall.pms.component;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.pms.annotation.GuliCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;


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

    ReentrantLock lock = new ReentrantLock();

    /**
     * 环绕通知：方法执行前，结束后，出现异常，正常返回都能通知
     *
     * @param joinPoint
     * @return
     */
    @Around("@annotation(com.atguigu.gulimall.pms.annotation.GuliCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {


        Object result = null;
        String prefix = "";
        try {
            log.info("缓存切面介入工作...前置通知");
            //获取目标方法的所有参数值
            Object[] args = joinPoint.getArgs();

            // 拿到注解的值
            // 方法一
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            GuliCache guliCache = signature.getMethod().getAnnotation(GuliCache.class);

            if (guliCache == null) {
                // 没有该注解，无需缓存，直接执行目标方法
                return joinPoint.proceed(args);

            }
            prefix = guliCache.prefix();
            log.info("获取到的注解的prefix值：{}" + prefix);

            // 方法二
//            String name = joinPoint.getSignature().getName();
//            for (Method method : joinPoint.getThis().getClass().getMethods()) {
//                if (method.getName().equals(name)) {
//                    GuliCache mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, GuliCache.class);
//                    log.info("获取到的注解的prefix值：{}" + mergedAnnotation.prefix());
//                }
//            }

            if (args != null) {
                for (Object arg : args) {
                    // 基本数据类型直接拼，对象可以用hashcode,拼的是参数中的id值
                    prefix += arg.toString();
                }
            }
            // spring.redisTemplate在高并发下就完蛋
            // jedis
            Object cache = getFromCache(signature, prefix);
            if (cache != null) {
                return cache;
            } else {
                lock.lock();
                //防止解锁后再去操作数据库，再检查一次，做到双检查
                cache = getFromCache(signature, prefix);
                if (cache == null) {
                    log.info("缓存没命中....查询数据库");
                    result = joinPoint.proceed(args);

                    long timeout = guliCache.timeout();
                    TimeUnit timeUnit = guliCache.TIME_UNIT();
                    redisTemplate.opsForValue().set(prefix, JSON.toJSONString(result), timeout, timeUnit);
                    log.info("缓存切面介入工作...方法执行完成,返回通知");
                    return result;
                } else {
                    return cache;
                }
            }

        } catch (Exception e) {
            log.info("缓存切面介入工作...异常通知");
            clearCurrentCache(prefix);
        } finally {
            log.info("缓存切面介入工作...后置通知");
            // 如果锁已被锁定了，则需要解锁
            if (lock.isLocked()) {
                lock.unlock();
            }

        }

        return null;
    }


    private Object getFromCache(Signature signature, String prefix) {
        String s = redisTemplate.opsForValue().get(prefix);
        if (!StringUtils.isEmpty(s)) {
            log.info("缓存命中...");
            // 当缓存中有值时，目标方法不用执行
            // 返回目标方法本该返回的对象类型
            Class returnType = ((MethodSignature) signature).getReturnType();   // 获取返回值类型,用JSON转化为目标对象类型
            return JSON.parseObject(s, returnType);
        }
        return null;
    }

    private void clearCurrentCache(String prefix) {
        redisTemplate.delete(prefix);
    }
}
