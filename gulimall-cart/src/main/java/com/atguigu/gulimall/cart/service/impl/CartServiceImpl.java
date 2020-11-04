package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.cart.feign.SkuCouponReductionFeignService;
import com.atguigu.gulimall.cart.feign.SkuFeignService;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.SkuCouponTo;
import com.atguigu.gulimall.cart.vo.CartItemVo;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.cart.vo.SkuCouponVo;
import com.atguigu.gulimall.cart.vo.SkuFullReductionVo;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuInfoVo;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;

import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Autowired
    SkuFeignService skuFeignService;

    @Autowired
    SkuCouponReductionFeignService skuCouponReductionFeignService;

    @Autowired
    @Qualifier("mainExecutor")
    ThreadPoolExecutor executor;


    /**
     * 获取购物车
     *
     * @param userKey
     * @param authentication
     * @return
     */
    @Override
    public CartVo getCart(String userKey, String authentication) throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        String redisKey;
        CartKey cartKey = getKey(userKey, authentication);
        // 设置在redis中保存的 用户的购物车的key
        if (!cartKey.isLogin()) {
            redisKey = Constant.TEMP_CART_PREFIX + cartKey.getKey();
        } else {
            // 已登录情况获取购物车 先合并,合并后获取到的是合并后的登录购物车
            mergeCart(userKey, Long.parseLong(cartKey.getKey()));
            redisKey = Constant.CART_PREFIX + cartKey.getKey();
        }

        List<CartItemVo> cartItems = getCartItems(redisKey);
        cartVo.setItems(cartItems);

        // 会触发线程去修改购物项的实时价格，会慢慢修改，但不会在本次及时返回
        CompletableFuture.runAsync(() -> {
            cartItems.forEach((item) -> {
                // 启动线程在后台更新价格
                // 远程批量查询接口，返回购物车中所有购物项的sku价格
                // 根据用户的cartKey修改用户的购物车

                String cache = redisTemplate.opsForValue().get(Constant.CACHE_SKU_INFO + item.getSkuId());

                if (StringUtils.isEmpty(cache)) {

                } else {
                    // 是从数据库缓存来的新数据
                    SkuInfoVo itemVo = JSON.parseObject(cache, SkuInfoVo.class);
                    // 根据缓存的商品信息改掉购物车中的信息
                    // item只是接口的返回内容，不是购物车对象
                    // item.setPrice(itemVo.getPrice());
                    RMap<String, String> rMap = redisson.getMap(Constant.CART_PREFIX + cartKey.getKey());
                    String s = rMap.get(item.getSkuId().toString());
                    CartItemVo cartItemVo = JSON.parseObject(s, CartItemVo.class);
                    cartItemVo.setPrice(itemVo.getPrice());

                    // 更新rMap中的hash
                    rMap.put(item.getSkuId().toString(), JSON.toJSONString(cartItemVo));
                }
            });
        }, executor);

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
    public CartVo addToCart(Long skuId, Integer num, String userKey, String authentication) throws ExecutionException, InterruptedException {
        CartKey key = getKey(userKey, authentication);
        String cartKey = key.getKey();
        String fullCartKey = "";
        // 1.获取购物车Rmap
        RMap<Object, Object> map;

        if (key.isLogin() && !StringUtils.isEmpty(userKey)) {
            // 购物车的第一层hash
            map = redisson.getMap(Constant.CART_PREFIX + cartKey);

            fullCartKey = Constant.CART_PREFIX + cartKey;
            // 登录状态，并且临时token不为空，需要合并操作时
            // 能进if判断，说明cartKey = userId
            mergeCart(userKey, Long.parseLong(cartKey));
        } else {
            // 购物车的第一层hash，未登录情况下,cartKey = key.getKey() 和 userKey 的值是一样的
            map = redisson.getMap(Constant.TEMP_CART_PREFIX + userKey);

            fullCartKey = Constant.TEMP_CART_PREFIX + cartKey;
        }

        // 添加购物车
        CartItemVo vo = addCartItemVo(skuId, num, fullCartKey);
        CartVo cartVo = new CartVo();
        if (!key.isLogin()) {
            // 没登录，每次都将临时购物车的userKey返回给前端,前端接受到就拿到并覆盖之前的userKey
            cartVo.setUserKey(cartKey);
        }
        cartVo.setItems(Arrays.asList(vo));

        // 如果未登录，购物车一个月过期，再次访问自动续期
        if (!key.isLogin()) {
            redisTemplate.expire(Constant.TEMP_CART_PREFIX + cartKey, Constant.TEMP_CART_TIMEOUT, TimeUnit.MINUTES);
        }

        // 其实前端只需要用户操作购物车的令牌，即 getKey方法生成的令牌，不管是已登录还是未登录的，让前端在下次请求时带上，就知道了用户的标识
        return cartVo;
    }

    @Override
    public CartVo updateCart(Long skuId, Integer num, String userKey, String authorization) {
        CartKey key = getKey(userKey, authorization);
        RMap<String, String> cart;
        String cartkey = key.getKey();

        if (key.isLogin()) {
            cart = redisson.getMap(Constant.CART_PREFIX + cartkey);
        } else {
            cart = redisson.getMap(Constant.TEMP_CART_PREFIX + cartkey);
        }

        String itemJson = cart.get(skuId.toString());
        CartItemVo itemVo = JSON.parseObject(itemJson, CartItemVo.class);
        itemVo.setNum(num);
        //修改购物车，覆盖redis数据；
        cart.put(skuId.toString(), JSON.toJSONString(itemVo));

        //获取购物车最新的所有购物项
        List<CartItemVo> cartItems = getCartItems(cartkey);
        CartVo cartVo = new CartVo();
        cartVo.setItems(cartItems);

        return cartVo;
    }

    @Override
    public CartVo checkCart(Long[] skuId, Integer status, String userKey, String authorization) {
        CartKey key = getKey(userKey, authorization);
        String cartKey = key.getKey();
        RMap<String, String> cart;
        if (key.isLogin()) {
            cart = redisson.getMap(Constant.CART_PREFIX + cartKey);
        } else {
            cart = redisson.getMap(Constant.TEMP_CART_PREFIX + cartKey);
        }
        if (skuId != null && skuId.length > 0) {
            // 遍历商品Id，因为在redis中存放的结构是以商品Id为key的
            for (Long sku : skuId) {
                String json = cart.get(sku.toString());
                CartItemVo itemVo = JSON.parseObject(json, CartItemVo.class);
                itemVo.setCheck(status == 0 ? false : true);
                //更新购物车中该购物项的状态，实现覆盖
                cart.put(sku.toString(), JSON.toJSONString(itemVo));
            }
        }

        //获取到这个购物车的所有记录
        List<CartItemVo> cartItems = getCartItems(cartKey);

        CartVo cartVo = new CartVo();
        cartVo.setItems(cartItems);

        return cartVo;
    }

    @Override
    public CartVo getCartForOrder(Long userId) {
        // 登陆情况下的购物车用的就是：cart:user:userId 作为key
        // 购物车结构<String<String,String>> 双层hash结构，从左到右依次为：userId -> SkuId -> SkuId对应的商品详情的json
        RMap<String, String> cart = redisson.getMap(Constant.CART_PREFIX + userId);

        List<CartItemVo> cartItemVos = new ArrayList<>();

        Collection<String> values = cart.values();
        if (values != null && values.size() > 0) {
            for (String innerValue : values) {
                if (StringUtils.isEmpty(innerValue)) {
                    CartItemVo itemVo = JSON.parseObject(innerValue, CartItemVo.class);
                    if (itemVo.isCheck()) {
                        cartItemVos.add(itemVo);
                    }
                }
            }
        }
        // 准备一个新的购物车对象用作方法返回
        CartVo cartVo = new CartVo();
        cartVo.setItems(cartItemVos);

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
            // Long userId = (Long) jwtBody.get("userId");
            Long userId = Long.parseLong(jwtBody.get("userId").toString());
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


    /**
     * 合并临时购物车和登录购物车
     *
     * @param userKey
     * @param userId
     */
    private void mergeCart(String userKey, Long userId) throws ExecutionException, InterruptedException {

        RMap<String, String> unloginMap = redisson.getMap(Constant.TEMP_CART_PREFIX + userKey);
        Collection<String> values = unloginMap.values();
        if (!Objects.isNull(values) && values.size() > 0) {
            for (String value : values) {
                CartItemVo itemVo = JSON.parseObject(value, CartItemVo.class);
                // 将临时购物车中的购物项添加到登录购物车中
                addCartItemVo(itemVo.getSkuId(), itemVo.getNum(), Constant.CART_PREFIX + userId);
            }

            // 清空临时购物车，或直接删除
            redisTemplate.delete(Constant.TEMP_CART_PREFIX + userKey);
        }
    }

    /**
     * 将商品添加到指定购物车
     *
     * @param skuId
     * @param num
     * @param cartKey redis中存放的最终的购物车的key值
     * @return
     */
    private CartItemVo addCartItemVo(Long skuId, Integer num, String cartKey) throws ExecutionException, InterruptedException {
        // 方法要返回的对象：向购物车中新增的商品
        CartItemVo vo = null;
        RMap<String, String> loginMap = redisson.getMap(cartKey);

        String cartItemVoJson = loginMap.get(skuId.toString());

        // 登录购物车中已经存在某款商品
        if (!StringUtils.isEmpty(cartItemVoJson)) {

            // 异步对购物车中数据进行修改（runAsync没有返回值）
            CompletableFuture<CartItemVo> future = CompletableFuture.supplyAsync(() -> {
                CartItemVo itemVo = JSON.parseObject(cartItemVoJson, CartItemVo.class);
                itemVo.setNum(itemVo.getNum() + num);
                /**
                 * 当购物车中的购物项的价格 与 商品的实时价格不同时，远程查询其实时价格
                 */
                Resp<SkuInfoVo> sKuInfoForCart = skuFeignService.getSKuInfoForCart(skuId);
                BigDecimal price = sKuInfoForCart.getData().getPrice();
                // 将实时价格重新覆盖
                itemVo.setPrice(price);
                loginMap.put(itemVo.getSkuId().toString(), JSON.toJSONString(itemVo));

                return itemVo;
            }, executor);

            // CartItemVo itemVo = future.get();
            vo = future.get();

        } else {
            /**
             * 当购物车中第一次加入某款商品时，此时存在“添加到购物车中的价格”，用于后期比较
             */
            CartItemVo itemVo = new CartItemVo();
            // 利用 3个CompletableFuture异步任务 将任务提交至线程池处理（ThreadPoolExecutor）
            CompletableFuture<Void> itemInfoAsync = CompletableFuture.runAsync(() -> {
                // 1.远程调用商品详情服务查询商品的详细信息
                SkuInfoVo skuInfoVo = skuFeignService.getSKuInfoForCart(skuId).getData();
                // 2.封装为购物车中的购物项
                BeanUtils.copyProperties(skuInfoVo, itemVo);
                itemVo.setNum(num);
                // 首次添加到购物车中的价格
                itemVo.setFirstPrice(skuInfoVo.getPrice());
            }, executor);


            // 3.TODO 商品优惠信息和商品满减信息也要远程查
            CompletableFuture<Void> couponAsync = CompletableFuture.runAsync(() -> {
                List<SkuCouponTo> couponTos = skuCouponReductionFeignService.getCoupons(skuId).getData();
                List<SkuCouponVo> couponVos = new ArrayList<>();
                if (!Objects.isNull(couponTos) && couponTos.size() > 0) {
                    for (SkuCouponTo couponTo : couponTos) {
                        SkuCouponVo skuCouponVo = new SkuCouponVo();
                        BeanUtils.copyProperties(couponTo, skuCouponVo);
                        couponVos.add(skuCouponVo);
                    }
                }
                itemVo.setCoupons(couponVos);
            }, executor);


            CompletableFuture<Void> reductionAsync = CompletableFuture.runAsync(() -> {
                List<SkuFullReductionVo> reductionVos = skuCouponReductionFeignService.getReduction(skuId).getData();
                if (!Objects.isNull(reductionVos) && reductionVos.size() > 0) {
                    itemVo.setReductions(reductionVos);
                }
            }, executor);

            // 等到三个异步任务都结束时，才能对itemVo进行保存
            CompletableFuture.allOf(itemInfoAsync, couponAsync, reductionAsync).get();
            // 保存到redis中,再次操作redis
            loginMap.put(skuId.toString(), JSON.toJSONString(itemVo));
            vo = itemVo;
        }

        return vo;
    }


    /**
     * 遍历购物车中的购物项并返回
     *
     * @param redisKey
     * @return
     */
    private List<CartItemVo> getCartItems(String redisKey) {
        List<CartItemVo> cartItemVos = new ArrayList<>();
        RMap<String, String> map = null;

        map = redisson.getMap(redisKey);
        Collection<String> values = map.values();

        if (!Objects.isNull(values) && values.size() > 0) {
            for (String value : values) {
                CartItemVo itemVo = JSON.parseObject(value, CartItemVo.class);
                cartItemVos.add(itemVo);
            }
        }
        return cartItemVos;
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
