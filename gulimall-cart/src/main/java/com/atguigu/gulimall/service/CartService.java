package com.atguigu.gulimall.service;

import com.atguigu.gulimall.vo.CartVo;

public interface CartService {

    CartVo getCart(String userKey, String authentication);

}
