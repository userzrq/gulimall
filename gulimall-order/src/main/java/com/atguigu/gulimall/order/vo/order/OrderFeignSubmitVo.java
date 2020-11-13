package com.atguigu.gulimall.order.vo.order;

import com.atguigu.gulimall.order.vo.CartVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 远程调用oms服务createAndSave发送的vo对象
 *
 * @author 10017
 */
@Data
public class OrderFeignSubmitVo {
    /**
     * 订单携带的Token
     */
    private String orderToken;
    /**
     * 订单的备注
     */
    private String remark;
    /**
     * 选中的地址
     */
    private Long addressId;
    /**
     * 提交的一个订单总额
     */
    private BigDecimal totalPrice;
    /**
     * 支付方式 0-在线付款 1-货到付款
     */
    private Integer payType;
    /**
     * 购物车VO
     */
    private CartVo cartVo;
    /**
     * 下单用户Id
     */
    private Long userId;
    /**
     * 收票人邮箱
     */
    private String billReceiverEmail;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 收货人电话
     */
    private String receiverPhone;
    /**
     * 收货人邮编
     */
    private String receiverPostCode;
    /**
     * 省份/直辖市
     */
    private String receiverProvince;
    /**
     * 城市
     */
    private String receiverCity;
    /**
     * 区
     */
    private String receiverRegion;
    /**
     * 详细地址
     */
    private String receiverDetailAddress;
}
