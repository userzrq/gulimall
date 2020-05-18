package com.atguigu.gulimall.pms.vo;

import lombok.Data;

@Data
public class BaseAttrVo {

    /**baseAttrs:[  //spu的基本信息
     *      {
     *      "attrId": 0, //属性id
     *      "attrName": "string", //属性名
     *      "valueSelected": [ "string" ] //属性值
     *      }
     *
     * ]
     *
     */
    private Long attrId;

    private String attrName;

    private String[]  valueSelected;
}
