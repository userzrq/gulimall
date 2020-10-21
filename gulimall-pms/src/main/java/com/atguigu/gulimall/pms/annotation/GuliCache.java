package com.atguigu.gulimall.pms.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author userzrq
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GuliCache {

    String prefix() default "cache";

    // 过期时间，默认三十分钟
    long timeout() default 60L * 30;

    // 过期时间的时间单位
    TimeUnit TIME_UNIT() default TimeUnit.SECONDS;
}
