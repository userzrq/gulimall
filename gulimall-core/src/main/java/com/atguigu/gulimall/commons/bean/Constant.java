package com.atguigu.gulimall.commons.bean;

public class Constant {

    public static final String ES_SPU_INDEX = "gulimall";

    public static final String ES_SPU_TYPE = "spu";

    /**
     * 商品信息缓存,冒号增加redis分层效果
     */
    public static final String CACHE_CATELOG = "cache:catelog:";

    /**
     * 购物车信息缓存，将购物车购物项详情信息缓存到redis
     */
    public static final String CACHE_SKU_INFO = "cache:skuinfo:";

    public static final String LOGIN_USER_PREFIX = "login:user:";

    /**
     * redis 用户登录信息的自动过期时间，60分钟没有请求发过来，在gateway校验令牌就自动过期
     */
    public static final Long LOGIN_USER_TIMEOUT_MINUTES = 60L;

    /**
     * redis 用户购物车前缀
     */
    public static final String CART_PREFIX = "cart:user:";

    /**
     * redis 用户临时购物车前缀
     */
    public static final String TEMP_CART_PREFIX = "cart:temp:";

    /**
     * redis 临时购物车过期时间，30天
     */
    public static final Long TEMP_CART_TIMEOUT = 60 * 24 * 30L;

    /**
     * 订单Token，防重复提交
     */
    public static final String ORDER_TOKEN = "cart:token:";

    /**
     * 订单Token过期时间，30分钟过期
     */
    public static final Long ORDER_TOKEN_TIMEOUT = 30L;

    /**
     * 商品库存分布式锁前缀 stock:lock: + skuId 标识每个商品的库存
     */
    public static final String STOCK_LOCKED = "stock:lock:";
}
