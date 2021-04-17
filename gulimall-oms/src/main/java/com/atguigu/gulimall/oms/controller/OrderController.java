package com.atguigu.gulimall.oms.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.order.OrderVo;
import com.atguigu.gulimall.oms.vo.OrderSubmitVo;
import com.rabbitmq.client.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.atguigu.gulimall.oms.service.OrderService;


/**
 * 订单
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:34:36
 */
@Api(tags = "订单 管理")
@Slf4j
@RestController
@RequestMapping("oms/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/bysn/{orderSn}")
    public Resp<OrderVo> getOrderInfo(@PathVariable("orderSn")  String orderSn) {

        OrderVo orderVo = orderService.getOrderInfoByOrderSn(orderSn);
        return Resp.ok(orderVo);
    }


    /**
     * 远程根据提交的订单创建订单信息
     *
     * @return
     */
    @PostMapping("/createAndSave")
    public Resp<OrderEntity> createAndSaveOrder(@RequestBody OrderSubmitVo orderSubmitVo) throws IOException {
        OrderEntity orderEntity = orderService.createAndSaveOrder(orderSubmitVo);

        if (!Objects.isNull(orderEntity)) {
            log.info("订单创建成功,订单详情:{}", orderEntity.toString());
        }
        return Resp.ok(orderEntity);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = orderService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    public Resp<OrderEntity> info(@PathVariable("id") Long id) {
        OrderEntity order = orderService.getById(id);

        return Resp.ok(order);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    public Resp<Object> save(@RequestBody OrderEntity order) {
        orderService.save(order);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    public Resp<Object> update(@RequestBody OrderEntity order) {
        orderService.updateById(order);

        return Resp.ok(null);
    }

    /**
     * 支付成功后根据订单号修改订单状态
     */
    @ApiOperation("支付成功后根据订单号修改订单状态")
    @PostMapping("/payed")
    public Resp<Object> updatePayed(@RequestBody OrderEntity order) {
        orderService.payedOrder(order);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    public Resp<Object> delete(@RequestBody Long[] ids) {
        orderService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
