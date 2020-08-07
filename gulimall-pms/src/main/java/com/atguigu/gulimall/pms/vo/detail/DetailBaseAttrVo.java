package com.atguigu.gulimall.pms.vo.detail;

import lombok.Data;

@Data
public class DetailBaseAttrVo {

    // 销售属性id
    private Long attrId;

    // 销售属性的名字
    private String attrName;

    private String[] attrValues;
}
