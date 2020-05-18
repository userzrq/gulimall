package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.pms.vo.BaseAttrVo;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SpuInfoService;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDao spuInfoDao;

    @Autowired
    private SpuImagesDao spuImagesDao;

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    // pms_product_attr_value
    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private SkuImagesDao skuImagesDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(QueryCondition queryCondition, Integer catId) {
        IPage<SpuInfoEntity> page = null;
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<SpuInfoEntity>();
        if(catId != 0){
            //如果catId为0，则是查全站
            wrapper.eq("catalog_id",catId);
            //WHERE catalog_id = ? AND ( spu_name LIKE ? OR id LIKE ? )
            if(!StringUtils.isEmpty(queryCondition.getKey())){
                wrapper.and(obj -> {
                    obj.like("spu_name",queryCondition.getKey());
                    obj.like("id",queryCondition.getKey());
                    return obj;
                });
            }
        }

        page = this.page(new Query<SpuInfoEntity>().getPage(queryCondition),wrapper);
        return new PageVo(page);
    }

    @Override
    public void saveSpuBigVo(SpuAllSaveVo spuAllVo) {

        // 1.保存spu的基本信息
        // 1.1 保存spu的基本信息
        Long spuId = this.saveSpuBaseInfo(spuAllVo);
        // 1.2 保存spu的图片信息
        this.saveSpuImages(spuId,spuAllVo.getSpuImages());

        // 2.保存spu的基本属性信息
        List<BaseAttrVo> baseAttrs = spuAllVo.getBaseAttrs();
        this.saveSpuBaseAttrs(spuId,baseAttrs);

        // 3.保存sku以及sku的营销相关信息
        this.saveSkuInfos(spuId,spuAllVo.getSkus());
    }


    @Override
    public Long saveSpuBaseInfo(SpuAllSaveVo spuAllVo) {

        // 1.保存spu的基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuAllVo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(new Date());
        spuInfoDao.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }

    @Override
    public void saveSpuImages(Long spuId, String[] spuImages) {
        StringBuffer imageUrl = new StringBuffer();
        for (String spuImage : spuImages) {
            imageUrl.append(spuImage);
            imageUrl.append(",");
        }
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setDecript(imageUrl.toString());
        spuInfoDescEntity.setSpuId(spuId);

        spuInfoDescDao.insert(spuInfoDescEntity);
    }

    @Override
    public void saveSpuBaseAttrs(Long spuId, List<BaseAttrVo> baseAttrs) {

        ArrayList<ProductAttrValueEntity> allSave = new ArrayList<>();

        for (BaseAttrVo baseAttr : baseAttrs) {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setAttrId(baseAttr.getAttrId());
            entity.setAttrName(baseAttr.getAttrName());
            String[] valueSelected = baseAttr.getValueSelected();
            entity.setAttrValue(AppUtils.arrayToStringWithSeperator(valueSelected,","));
            // 注入默认值
            entity.setAttrSort(0);
            entity.setQuickShow(1);
            entity.setSpuId(spuId);

            allSave.add(entity);
        }

        productAttrValueDao.insertBatch(allSave);
    }

    // 保存sku的所有详情
    @Override
    public void saveSkuInfos(Long spuId, List<SkuVo> skus) {
        // 0.查出spu信息：进方法的所以sku属于同一个spu下
        SpuInfoEntity spuInfoEntity = this.getById(spuId);
        Long id = spuInfoEntity.getId();
        // catalog_id  brand_id
        // 1.保存sku的info信息
        for (SkuVo skuVo : skus) {
            String[] images = skuVo.getImages();
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0,5).toUpperCase());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            if(images != null && images.length > 0){
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }
            skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuSubtitle(skuVo.getSkuSubtitle());
            skuInfoEntity.setWeight(skuVo.getWeight());
            skuInfoEntity.setPrice(skuVo.getPrice());

            skuInfoDao.insert(skuInfoEntity);

            // 2.保存sku的所有对应图片
            Long skuId = skuInfoEntity.getSkuId();
            for (int i = 0; i < images.length; i++) {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setDefaultImg(i == 0 ? 1 : 0);
                skuImagesEntity.setImgUrl(images[i]);
                skuImagesEntity.setImgSort(0);
                skuImagesDao.insert(skuImagesEntity);
            }

            // 3.当前sku的所有销售属性组合保存起来
        }
    }


}