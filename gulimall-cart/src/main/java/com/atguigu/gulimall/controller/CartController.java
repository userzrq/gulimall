package com.atguigu.gulimall.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.service.CartService;
import com.atguigu.gulimall.vo.CartVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "购物车系统")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 以后购物车的所有操作，前端最多会携带两个令牌
     * 1)登陆后的jwt放在请求头的"Authentication"字段（不一定会带）
     * 2)只要前端收到服务器响应的 userKey ，以后所有操作购物车的操作都会带上这个参数
     * @return
     */
    @ApiOperation("获取购物车中的数据")
    @GetMapping("/list")
    public Resp<CartVo> getCart(String userKey,
                                @RequestHeader(name = "Authentication",required = false) String authentication) {

        CartVo cartVo = cartService.getCart(userKey,authentication);

        return null;
    }


    /**
     * @param skuId
     * @param num
     * @param userKey  临时用户的令牌，如果有就传
     * @param request
     * @return
     */
    @ApiOperation("将某个sku添加到购物车")
    @GetMapping("/add/{skuId}")
    public Resp<Object> addToCart(@PathVariable(value = "skuId") Long skuId,
                                  Integer num,
                                  String userKey,
                                  HttpServletRequest request) {

        String authentication = request.getHeader("Authentication");

        return null;
    }
}
