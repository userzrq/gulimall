package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.pms.vo.detail.CouponsVo;
import com.atguigu.gulimall.pms.vo.detail.DetailAttrGroup;
import com.atguigu.gulimall.pms.vo.detail.DetailSaleAttrVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuItemDetailVo {
    // 1.当前sku的基本信息

    private Long skuId;

    private Long spuId;

    private Long catalogId;

    private Long brandId;

    private String skuTitle;

    private String skuSubtitle;

    private BigDecimal price;

    private BigDecimal weight;
    // 2.当前sku的所有图片
    private List<String> pics;

    // 3.当前sku的所有优惠促销信息
    private List<CouponsVo> coupons;

    // 4.当前sku的所有可供选择的销售属性组合
    private List<DetailSaleAttrVo> saleAttrs;

    // 5.spu的所有基本属性
    private List<DetailAttrGroup> baseAttrs;

    // 6.spu详情介绍
    private SpuInfoDescEntity desc;
}
