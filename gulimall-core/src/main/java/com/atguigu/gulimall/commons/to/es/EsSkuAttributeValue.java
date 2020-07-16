package com.atguigu.gulimall.commons.to.es;

import lombok.Data;

/**
 * 商品和属性的关联关系vo
 */
@Data
public class EsSkuAttributeValue {

    /**
     * 商品和属性关联的数据表的主键id
     */
    private Long id;

    /**
     * 当前sku对应的属性attr_id
     */
    private Long productAttributeId;
    /**
     * 对应属性的名称 电池
     */
    private String name;
    /**
     * 对应属性的属性值 3000mah
     */
    private String value;
    /**
     * 某个属性关联关系对应的spuId
     */
    private Long spuId;



}
