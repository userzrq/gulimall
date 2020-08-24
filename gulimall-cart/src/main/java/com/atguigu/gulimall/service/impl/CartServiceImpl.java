package com.atguigu.gulimall.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.service.CartService;
import com.atguigu.gulimall.vo.CartItemVo;
import com.atguigu.gulimall.vo.CartVo;
import jdk.nashorn.internal.ir.CatchNode;
import lombok.Data;
import org.apache.tomcat.util.bcel.Const;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    /**
     * 获取购物车
     *
     * @param userKey
     * @param authentication
     * @return
     */
    @Override
    public CartVo getCart(String userKey, String authentication) {

        CartVo cartVo = new CartVo();
        String redisKey;
        ArrayList<CartItemVo> cartItemVos = new ArrayList<>();

        CartKey cartKey = getKey(userKey, authentication);
        // 设置在redis中保存的 用户的购物车的key
        if (cartKey.isTemp()) {
            redisKey = Constant.TEMP_CART_PREFIX + cartKey.getKey();    // 不一定用得到
        } else {
            redisKey = Constant.CART_PREFIX + cartKey.getKey();
        }

        RMap<Object, Object> map = redisson.getMap(redisKey);

        // 判断购物车是够需要合并
        if (cartKey.isMerge()) {
            // getKey 拿到的key是用户id，那么合并还需要一个临时token对应的redis key
        } else {
            Collection<Object> values = map.values();

            if (!Objects.isNull(values) && values.size() > 0) {
                for (Object value : values) {
                    String json = (String) value;
                    CartItemVo itemVo = JSON.parseObject(json, CartItemVo.class);
                    cartItemVos.add(itemVo);
                }
            }
        }

        cartVo.setItems(cartItemVos);

        return cartVo;
    }


    private CartKey getKey(String userKey, String authentication) {
        CartKey cartKey = new CartKey();
        String key;

        if (!StringUtils.isEmpty(authentication)) {
            // 登陆了,就取出 jwt 中的用户信息，封装到 userKey 中
            Map<String, Object> jwtBody = GuliJwtUtils.getJwtBody(authentication);
            Long userId = (Long) jwtBody.get("userId");
            key = userId + "";
            cartKey.setKey(key);
            cartKey.setLogin(true);
            if (!StringUtils.isEmpty(userKey)) {
                // 在有登陆的jwt的同时还有临时token
                cartKey.setMerge(true);
            }
        } else {
            // 没登录的情况
            if (!StringUtils.isEmpty(userKey)) {
                key = userKey;
                cartKey.setKey(key);
                cartKey.setLogin(false);
                cartKey.setMerge(false);
            } else {
                // 既没有登陆也没用临时键(第一次访问时，要生成一个临时token)
                key = UUID.randomUUID().toString().replace("-", "");
                cartKey.setLogin(false);
                cartKey.setMerge(false);
                cartKey.setTemp(true); // 这是一个临时的token
            }
        }
        cartKey.setKey(key);

        return cartKey;
    }

}


@Data
class CartKey {
    // 决定用临时购物车的 key 还是登陆 authentication 的值
    private String key;
    private boolean login;
    private boolean temp;
    // 判断购物车是否需要合并
    private boolean merge;
}
