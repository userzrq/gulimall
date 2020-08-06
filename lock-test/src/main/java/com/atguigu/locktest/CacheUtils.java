package com.atguigu.locktest;

import java.util.HashMap;
import java.util.Map;

import com.atguigu.locktest.bean.User;
import org.springframework.beans.BeanUtils;

public class CacheUtils {

    public static Map<String, User> map = new HashMap<>();

    /**
     * 从缓存中拿到的对象是一个引用，在缓存外动了，即使不存储，对象也会发生改变
     * 读时复制
     *
     * @param username
     * @return
     */
    public static User getFromCache(String username) {
        User user = map.get(username);
        User user1 = new User();
        // 创建一个新的对象，将原来的引用切断
        BeanUtils.copyProperties(user, user1);
        return user1;
    }

    public static void saveToCache(User user) {
        User user1 = new User();
        BeanUtils.copyProperties(user, user1);
        map.put(user1.getUsername(), user1);

    }

    /**
     * 清除某个key的缓存的方法
     *
     * @param key
     */
    public static void removeFromCache(String key) {
        map.remove(key);
    }

}
