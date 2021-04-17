package com.atguigu.gulimall.order.controller;


import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.payment.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付宝支付成功后的回调
 *
 * @author 10017
 */
@RestController
@RequestMapping("/order")
public class PayController {

    @Autowired
    private OrderService orderService;


    /**
     * 支付成功后的回调接口
     *
     * @param vo
     * @return
     */
    @RequestMapping("/pay/alipay/success")
    public String paySuccess(PayAsyncVo vo) {

        orderService.paySuccess(vo);

        return "success";
    }
}
