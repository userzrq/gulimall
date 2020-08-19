package com.atguigu.gulimall.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "购物车系统")
@RestController
@RequestMapping("/cart")
public class CartController {

    @ApiOperation("获取购物车中的数据")
    @GetMapping("/list")
    public Resp<Object> getCart(){
        return null;
    }


    @ApiOperation("将某个sku添加到购物车")
    @GetMapping("/add/{skuId}")
    public Resp<Object> addToCart(@PathVariable(value ="skuId") Long skuId){
        return null;
    }
}
