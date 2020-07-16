package com.atguigu.gulimall.commons.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 以Sku为单位存在es中
 * <p>
 * 1、sku的基本信息
 * 2、sku的品牌、分类等
 * 3、sku的检索属性信息
 */
@Data
public class EsSkuVo {

    /**
     * skuId
     */
    private Long id;
    private Long brandId;
    private String brandName;
    private Long productCategoryId;
    private String productCategoryName;

    /**
     * sku的默认图片
     */
    private String pic;
    /**
     * 需要检索的sku标题
     */
    private String name;
    private BigDecimal price;
    /**
     * 销量
     */
    private Integer sale;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * 排序 热度（经常被搜索或者买了热度，排序应该靠前，排序分应该高）
     */
    private Integer sort;


    /**
     * 保存当前sku所有需要检索的属性
     * 检索属性来源于sku的全部属性【基本属性，销售属性】中 search_type = 1的
     */
    private List<EsSkuAttributeValue> attrValueList;    //商品的筛选属性（SPU的属性）


    /**
     *     private Long id;
     *     private Long brandId;
     *     private String brandName;
     *     private Long productCategoryId;
     *     private String productCategoryName;
     *     private String pic;
     *     private String name;
     *     private BigDecimal price;
     *     private Integer sale;
     *     private Integer stock;
     *     private Integer sort;
     *     props:[
     *      {id:1,attrId:25,attrName:"内存",value:"4g"}
     *      {id:8,attrId:33,attrName:"电池",value:"3000mah"}
     *      {id:13,attrId:39,attrName:"cpu核数",value:"8核"}
     *     ]
     *
     *
     *
     *
     */
}
