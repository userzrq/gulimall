package com.atguigu.gulimall.pms.vo.detail;

import lombok.Data;

@Data
public class CouponsVo {

    /**
     * 促销信息、优惠券的名字
     */
    private String name;


    /**
     * 促销类型
     *  0-优惠券
     *  1-满减
     *  2-阶梯价格
     */
    private Integer type;
}
