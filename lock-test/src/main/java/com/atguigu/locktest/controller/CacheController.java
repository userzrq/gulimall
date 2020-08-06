package com.atguigu.locktest.controller;

import com.atguigu.locktest.CacheUtils;
import com.atguigu.locktest.bean.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CacheController {


    @GetMapping("/get")
    public User getUser(@RequestParam("username") String username) {
        User cache = CacheUtils.getFromCache(username);
        if (cache == null) {
            // 模拟从数据库中查出的数据
            return new User("dbuser", "dbuser@qq.com");
        }

        return cache;
    }

    @GetMapping("/insert")
    public String getUser(User user) {
        // 1.双写模式 写缓存
        CacheUtils.saveToCache(user);
        // 2.写数据库
        System.out.println("往数据库中写数据..." + user);

        return "ok";
    }

    @GetMapping("/update")
    public User updateUser(String username, String email) {
        User cache = new User(username, email);
        // 1.双写模式
        // map中key值相同就直接覆盖了
        CacheUtils.saveToCache(cache);

        // 2.更新数据库（也需要写时复制）

        return cache;
    }

    @GetMapping("/info")
    public String userInfo(String username) {
        User cache = CacheUtils.getFromCache(username);
        if (cache == null) {
            cache = new User("dbuser", "dbuser@qq.com");
        }
        // map中的value对象为引用对象
        // 进程内的缓存，即使没有map缓存中的数据进行修改，但缓存的数据是一个引用
        // 造成脏数据（没有真正修改数据库，但是缓存却变了）
        cache.setEmail("zhangsan@qqqqqqqq.com");
        return "ok";
    }
}
