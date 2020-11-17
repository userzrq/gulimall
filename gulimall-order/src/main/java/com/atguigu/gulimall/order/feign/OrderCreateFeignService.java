package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.order.OrderCloseVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import com.atguigu.gulimall.order.vo.order.OrderFeignSubmitVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PostMapping("/oms/order/createAndSave")
    public Resp<OrderEntityVo> createAndSaveOrder(@RequestBody OrderFeignSubmitVo orderFeignSubmitVo);

    /**
     * 订单详情查询
     *
     * @param id
     * @return
     */
    @GetMapping("/oms/order/info/{id}")
    public Resp<OrderEntityVo> info(@PathVariable("id") Long id);


    /**
     * 关闭订单
     *
     * @param orderCloseVo
     * @return
     */
    @PostMapping("/oms/order/update")
    public Resp<Object> closeOrder(@RequestBody OrderCloseVo orderCloseVo);


    /**
     * 更新订单
     *
     * @param order
     * @return
     */
    @PostMapping("/oms/order/update")
    public Resp<Object> updateOrder(@RequestBody OrderEntityVo order);
}
