package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "购物车系统")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;




    @ApiOperation("更新购物车商品数量")
    @PostMapping("/update")
    public Resp<CartVo> updateCart(@RequestParam(name = "skuId",required = true) Long skuId,
                                   @RequestParam(name = "num",defaultValue = "1") Integer num,
                                   String userKey,
                                   @RequestHeader(name = "Authorization",required = false) String authorization){

        CartVo cartVo = cartService.updateCart(skuId,num,userKey,authorization);

        return Resp.ok(cartVo);
    }

    /**
     * 以后购物车的所有操作，前端最多会携带两个令牌
     * 1)登陆后的jwt放在请求头的"Authentication"字段（不一定会带）
     * 2)只要前端收到服务器响应的 userKey ，以后所有操作购物车的操作都会带上这个参数
     *
     * @return
     */
    @ApiOperation("获取购物车中的数据")
    @GetMapping("/list")
    public Resp<CartVo> getCart(String userKey,
                                @RequestHeader(name = "Authentication", required = false) String authentication) {

        CartVo cartVo = cartService.getCart(userKey, authentication);

        return null;
    }


    /**
     * @param skuId
     * @param num
     * @param userKey        临时用户的令牌，如果有就传
     * @param authentication
     * @return
     */
    @ApiOperation("将某个sku添加到购物车")
    @GetMapping("/add")
    public Resp<Object> addToCart(@RequestParam(name = "skuId", required = true) Long skuId,
                                  @RequestParam(name = "num", defaultValue = "1") Integer num,
                                  String userKey,
                                  @RequestHeader(name = "Authentication", required = false) String authentication) {

        // authentication = request.getHeader("Authentication");  Authentication header的值直接由注解获取

        CartVo cartVo = cartService.addToCart(skuId, num, userKey, authentication);

        Map<String, Object> map = new HashMap<>();
        // 当用户登录时，userKey为null，未登录时，会将临时用户令牌传给前端
        map.put("userKey", cartVo.getUserKey());
        map.put("item",cartVo.getItems());

        // 操作成功时返回给前端操作的用户标识，前端下次请求时带上，即能操作同一个购物车
        return Resp.ok(map);
    }
}
