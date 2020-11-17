package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.order.OrderEntityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 用户勾选购物车中的商品后，点击提交订单
     * service层远程去查用户所有的收货地址和购物车中选中的商品（购物车中的更新是redis的同值key覆盖操作）
     * 返回订单确认VO
     * 前端返给用户订单确认页面，让用户选择收货地址等
     *
     * @return
     */
    @GetMapping
    public Resp<OrderConfirmVo> orderConfirm(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<String, Object> jwtBody = GuliJwtUtils.getJwtBody(authorization);
        Long userId = (Long) jwtBody.get("userId");

        OrderConfirmVo confirmVo = orderService.confirmOrderData(userId);

        return Resp.ok(confirmVo);
    }

    /**
     * 确认订单后提交订单，提交的订单需要重新计算价格
     *
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submit")
    public Resp<Object> submitOrder(@RequestBody OrderSubmitVo orderSubmitVo,
                                    HttpServletRequest request) throws ExecutionException, InterruptedException {
        Long userId = getCurrentUserId(request);
        Resp<Object> resp = orderService.submitOrder(orderSubmitVo, userId);

        Object data = resp.getData();
        if (data instanceof OrderEntityVo) {
            // 订单提交成功
            // 生成一个支付页（拉起支付宝支付页面）
        }
        return resp;
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

//    /**
//     * 创建订单
//     * @return
//     */
//    @GetMapping("/create")
//    public Order createOrder() {
//        Order order = orderService.createOrder();
//        return order;
//    }
}
