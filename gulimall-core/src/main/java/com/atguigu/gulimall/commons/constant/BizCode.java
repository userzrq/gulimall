package com.atguigu.gulimall.commons.constant;


/**
 * 常用返回状态码
 *
 * @author 10017
 */
public enum BizCode {

    /**
     * 令牌失效-拒绝访问
     */
    TOKEN_INVALID(40003, "令牌失效"),
    /**
     * 订单数据
     */
    ORDER_NEED_REFRESH(41000, "商品数据有修改，请重新提交再试"),
    /**
     * 远程服务故障，调度失败
     */
    SERVICE_UNAVAILABLE(10000, "远程服务故障，调度失败"),

    /**
     * 库存不足，锁库存失败
     */
    STOCK_NOT_ENOUGH(50001, "库存不足");


    Integer code;
    String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    BizCode(Integer code, String msg) {

        this.code = code;
        this.msg = msg;
    }
}
