package com.atguigu.gulimall.cart.service;


import com.atguigu.gulimall.cart.vo.CartVo;

public interface CartService {

    CartVo getCart(String userKey, String authentication);

    CartVo addToCart(Long skuId, Integer num, String userKey, String authentication);

    CartVo updateCart(Long skuId, Integer num, String userKey, String authorization);

    CartVo checkCart(Long[] skuId, Integer status, String userKey, String authorization);

}
