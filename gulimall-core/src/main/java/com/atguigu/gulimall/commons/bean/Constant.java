package com.atguigu.gulimall.commons.bean;

public class Constant {

    public static final String ES_SPU_INDEX = "gulimall";

    public static final String ES_SPU_TYPE = "spu";

    /**
     * 商品信息缓存,冒号增加redis分层效果
     */
    public static final String CACHE_CATELOG = "cache:catelog:";

    public static final String LOGIN_USER_PREFIX = "login:user:";

    /**
     * redis 用户登录信息的自动过期时间，60分钟没有请求发过来，在gateway校验令牌就自动过期
     */
    public static final Long LOGIN_USER_TIMEOUT_MINUTES = 60L;
}
