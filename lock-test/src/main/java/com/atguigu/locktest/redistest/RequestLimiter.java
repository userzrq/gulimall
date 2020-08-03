package com.atguigu.locktest.redistest;

import org.springframework.beans.factory.annotation.Autowired;

public class RequestLimiter {

//    @Autowired
//    private jedisclient
//
//    public static boolean flowControl(String key){
//
//        int max = 100;
//        long total =1L;
//        try {
//            if (jedisInstance.get(key) == null) {
//                //jedisInstance是Jedis连接实例，可以使单链接也可以使用链接池获取，实现方式请参考之前的blog内容
//                //如果redis目前没有这个key，创建并赋予0，有效时间为60s
//                jedisInstance.setex(key, 60, "0");
//            } else {
//                //获取加1后的值
//                total = jedisInstance.incr(redisKey).longValue();
//                //Redis TTL命令以秒为单位返回key的剩余过期时间。当key不存在时，返回-2。当key存在但没有设置剩余生存时间时，返回-1。否则，以秒为单位，返回key的剩余生存时间。
//                if (jedisInstance.ttl(redisKey).longValue() == -1L)
//                {
//                    //为给定key设置生存时间，当key过期时(生存时间为0)，它会被自动删除。
//                    jedisInstance.expire(redisKey, 60);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("流量控制组件:执行计数操作失败,无法执行计数");
//        }
//        long keytotaltransations = max;
//        //判断是否已超过最大值，超过则返回false
//        if (total > keytotaltransations) {
//            return false;
//        }
//        return true;
//    }
}
