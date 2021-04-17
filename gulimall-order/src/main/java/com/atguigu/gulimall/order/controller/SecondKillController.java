package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.constant.BizCode;
import com.atguigu.gulimall.commons.constant.RabbitMQConstant;
import com.atguigu.gulimall.commons.constant.RedisPrefixConstant;
import com.atguigu.gulimall.commons.to.mq.OrderItemMqTo;
import com.atguigu.gulimall.commons.to.mq.OrderMqTo;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 商品秒杀前端控制器类
 *
 * @author 10017
 */
@RestController
@RequestMapping("/order")
public class SecondKillController {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/miaosha/pay")
    public String payKillOrder(HttpServletRequest request, String orderSn) throws InterruptedException {

        RCountDownLatch latch = redisson.getCountDownLatch(RedisPrefixConstant.MIAO_SHA_ORDERSN_COUNTDOWN + orderSn);
        latch.await();

        // 远程闭锁countDown后，再查询订单
        return "";
    }


    @GetMapping("/miaosha/{skuId}")
    public Resp<Object> kill(@PathVariable("skuId") Long skuId, HttpServletRequest request) {

        Long userId = getCurrentUserId(request);

        if (!Objects.isNull(userId)) {
            // 获取分布式信号量之前，要通过其他接口取设置分布式信号量的值（允许秒杀数量）
            // 分布式信号量
            RSemaphore semaphore = redisson.getSemaphore(RedisPrefixConstant.MIAO_SHA_PREFIX + skuId);

            // 在减量操作之前，也要获取到semaphore，并设置秒杀库存量
            // semaphore.trySetPermits(100);

            // 尝试从信号量中减量
            boolean b = semaphore.tryAcquire();
            if (b) {
                // 创建订单
                OrderMqTo mqTo = new OrderMqTo();
                String orderSn = IdWorker.getTimeId();
                mqTo.setOrderSn(orderSn);
                mqTo.setMemberId(userId);
                OrderItemMqTo orderItemMqTo = new OrderItemMqTo();
                orderItemMqTo.setSkuId(skuId);
                orderItemMqTo.setOrderSn(orderSn);

                mqTo.setOrderItems(Arrays.asList(orderItemMqTo));
                // 准备闭锁（订单号,控制细粒度）
                RCountDownLatch latch = redisson.getCountDownLatch(RedisPrefixConstant.MIAO_SHA_ORDERSN_COUNTDOWN + orderSn);
                latch.trySetCount(1);

                rabbitTemplate.convertAndSend(RabbitMQConstant.order_exchange, RabbitMQConstant.order_quick_create_routing_key, mqTo);
                Resp<Object> ok = Resp.ok(null);
                ok.setMsg(BizCode.KILL_SUCCESS.getMsg());
                ok.setCode(BizCode.KILL_SUCCESS.getCode());
                // 前端返回秒杀成功的订单号
                ok.setData(orderSn);
                return ok;
            } else {
                Resp<Object> fail = Resp.fail(null);
                fail.setCode(BizCode.TOO_MANY_PEOPLE.getCode());
                fail.setMsg(BizCode.TOO_MANY_PEOPLE.getMsg());
            }
        }
        Resp<Object> fail = Resp.fail(null);
        fail.setCode(BizCode.NEED_LOGIN.getCode());
        fail.setMsg(BizCode.NEED_LOGIN.getMsg());

        return fail;
    }


    /**
     * 从request请求头中获取用户身份
     *
     * @param request
     * @return
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<String, Object> jwtBody = GuliJwtUtils.getJwtBody(authorization);
        Long userId = (Long) jwtBody.get("userId");

        return userId;
    }
}
