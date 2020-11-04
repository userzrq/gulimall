package com.atguigu.gulimall.cart.service;


import com.atguigu.gulimall.cart.vo.CartVo;

import java.util.concurrent.ExecutionException;

public interface CartService {

    /**
     * 获取购物车中的数据
     * @param userKey
     * @param authentication
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartVo getCart(String userKey, String authentication) throws ExecutionException, InterruptedException;

    /**
     * 将某个sku加入到购物车中
     * @param skuId
     * @param num
     * @param userKey
     * @param authentication
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartVo addToCart(Long skuId, Integer num, String userKey, String authentication) throws ExecutionException, InterruptedException;

    /**
     * @ApiOperation("更新购物车商品数量")
     * @param skuId
     * @param num
     * @param userKey
     * @param authorization
     * @return
     */
    CartVo updateCart(Long skuId, Integer num, String userKey, String authorization);

    /**
     * 选中/不选中购物车中的购物项
     * @param skuId
     * @param status
     * @param userKey
     * @param authorization
     * @return
     */
    CartVo checkCart(Long[] skuId, Integer status, String userKey, String authorization);

    /**
     * 获取当前用户购物车中选中的商品及其他信息等
     * @param userId
     * @return
     */
    CartVo getCartForOrder(Long userId);
}
