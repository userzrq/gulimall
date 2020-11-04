package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.CartVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient("gulimall-cart")
public interface CartFeignService {

    /**
     * 远程调用 获取购物车中勾选的商品详情和优惠信息
     * 在Feign调用时是不需要传Request对象的
     *
     * @param
     * @return
     */
    @GetMapping("/cart/getItemsForOrder")
    public Resp<CartVo> getCartCheckItemsAndStatics();
}
