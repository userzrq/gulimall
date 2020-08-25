package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.cart.feign.SkuFeignService;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItemVo;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.to.SkuInfoVo;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;

import lombok.Data;
import org.apache.tomcat.util.bcel.Const;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    SkuFeignService skuFeignService;

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


    /**
     * 往购物车中添加商品，购物车分临时购物车和在线购物车
     *
     * @param skuId
     * @param num
     * @param userKey        临时购物车的用户表示
     * @param authentication 封装了userId的在线购物车token
     */
    @Override
    public CartVo addToCart(Long skuId, Integer num, String userKey, String authentication) {
        CartKey key = getKey(userKey, authentication);
        String cartKey = key.getKey();
        // 1.获取购物车Rmap
        RMap<Object, Object> map = redisson.getMap(Constant.CART_PREFIX + cartKey);    // 购物车的第一层hash

        // 2.添加购物车之前先确定购物车中有没有这个商品，如果没有就新增，如果有就数量+num
        String item = (String) map.get(skuId.toString());        // 购物车的第二层hash,拿到的String是CartItemVo的Json串
        if (!StringUtils.isEmpty(item)) {
            // 购物车中原来就有此商品 ,在原先的数量上 +num 并重新储存
            CartItemVo itemVo = JSON.parseObject(item, CartItemVo.class);
            itemVo.setNum(itemVo.getNum() + num);
            map.put(skuId.toString(), JSON.toJSONString(itemVo));
        } else {
            // 远程调用商品详情服务查询商品的详细信息
            SkuInfoVo skuInfoVo = skuFeignService.getSKuInfoForCart(skuId).getData();
            CartItemVo itemVo = new CartItemVo();
            // 封装为购物车中的购物项
            BeanUtils.copyProperties(skuInfoVo, itemVo);
            itemVo.setNum(num);

            // TODO 商品优惠信息和商品满减信息也要远程查

            // 保存到redis中
            map.put(skuId.toString(), JSON.toJSON(itemVo));
        }

        CartVo cartVo = new CartVo();
        cartVo.setUserKey(cartKey);

        // 其实前端只需要用户操作购物车的令牌，即 getKey方法生成的令牌，不管是已登录还是未登录的，让前端在下次请求时带上，就知道了用户的标识
        return cartVo;
    }


    /**
     * 返回一个购物车后缀标识，跟前缀拼接后是一个 redis 的 key
     * 1）、只要登录了返回的是用户id，拼上 cart:user: 变成 cart:user:userId
     * 2）、没登录返回临时的购物车key 拼上 cart:temp: 变成 cart:temp:uuid
     *
     * @param userKey
     * @param authentication
     * @return
     */
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
