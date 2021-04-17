package com.atguigu.gulimall.cart.service;


import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.cart.vo.ClearCartVo;

import java.util.concurrent.ExecutionException;

/**
 * @author 10017
 */
public interface CartService {

    /**
     * 获取购物车中的数据
     *
     * @param userKey 用户临时令牌
     * @param authentication
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartVo getCart(String userKey, String authentication) throws ExecutionException, InterruptedException;

    /**
     * 将某个sku加入到购物车中
     *
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
     * @param skuId
     * @param num
     * @param userKey
     * @param authorization
     * @return
     * @ApiOperation("更新购物车商品数量")
     */
    CartVo updateCart(Long skuId, Integer num, String userKey, String authorization);

    /**
     * 选中/不选中购物车中的购物项
     *
     * @param skuId
     * @param status
     * @param userKey
     * @param authorization
     * @return
     */
    CartVo checkCart(Long[] skuId, Integer status, String userKey, String authorization);

    /**
     * 获取当前用户购物车中选中的商品及其他信息等
     *
     * @param userId
     * @return
     */
    CartVo getCartForOrder(Long userId);


    /**
     * 生成订单后删除购物车中提交的商品
     *
     * @param clearCartVo
     */
    void clearSkuIds(ClearCartVo clearCartVo);
}
