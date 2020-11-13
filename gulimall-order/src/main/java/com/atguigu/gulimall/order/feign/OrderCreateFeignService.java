package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.atguigu.gulimall.order.vo.order.OrderFeignSubmitVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 10017
 */
@FeignClient("gulimall-oms")
public interface OrderCreateFeignService {

    /**
     * 远程根据提交的订单创建订单信息
     *
     * @param orderFeignSubmitVo
     * @return
     */
    @PostMapping("oms/order/createAndSave")
    public Resp<OrderEntityVo> createAndSaveOrder(@RequestBody OrderFeignSubmitVo orderFeignSubmitVo);
}
