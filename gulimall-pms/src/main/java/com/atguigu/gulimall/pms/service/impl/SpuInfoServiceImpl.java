package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.commons.to.SkuStockVo;
import com.atguigu.gulimall.commons.to.es.EsSkuAttributeValue;
import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.pms.feign.EsFeignService;
import com.atguigu.gulimall.pms.feign.SmsSkuSaleInfoFeignService;
import com.atguigu.gulimall.pms.feign.WmsFeignService;
import com.atguigu.gulimall.pms.vo.BaseAttrVo;
import com.atguigu.gulimall.pms.vo.SaleAttrVo;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import com.atguigu.gulimall.commons.to.es.EsSkuVo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
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

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Autowired
    private WmsFeignService wmsFeignService;

    @Autowired
    private SmsSkuSaleInfoFeignService smsSkuSaleInfoFeifnService;

    @Autowired
    private EsFeignService esFeignService;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

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
        if (catId != 0) {
            //如果catId为0，则是查全站
            wrapper.eq("catalog_id", catId);
            //WHERE catalog_id = ? AND ( spu_name LIKE ? OR id LIKE ? )
            if (!StringUtils.isEmpty(queryCondition.getKey())) {
                wrapper.and(obj -> {
                    obj.like("spu_name", queryCondition.getKey());
                    obj.like("id", queryCondition.getKey());
                    return obj;
                });
            }
        }

        page = this.page(new Query<SpuInfoEntity>().getPage(queryCondition), wrapper);
        return new PageVo(page);
    }


    /**
     * 保存大vo的方法，其中调用了多个方法,需要用分布式事务管理这整个流程
     *
     * @param spuAllVo
     */
    @GlobalTransactional(rollbackFor = {Exception.class})
    @Override
    public void saveSpuBigVo(SpuAllSaveVo spuAllVo) {

        //获取代理对象,this不会触发新的事务传播特性，使用代理对象调用方法
        SpuInfoService proxy = (SpuInfoService) AopContext.currentProxy();

        // 1.保存spu的基本信息
        // 1.1 保存spu的基本信息
        //Long spuId = this.saveSpuBaseInfo(spuAllVo);
        Long spuId = proxy.saveSpuBaseInfo(spuAllVo);

        // 1.2 保存spu的图片信息
        //this.saveSpuImages(spuId,spuAllVo.getSpuImages());
        proxy.saveSpuImages(spuId, spuAllVo.getSpuImages());

        // 2.保存spu的基本属性信息
        List<BaseAttrVo> baseAttrs = spuAllVo.getBaseAttrs();
        //this.saveSpuBaseAttrs(spuId,baseAttrs);
        proxy.saveSpuBaseAttrs(spuId, baseAttrs);

        // 3.保存sku以及sku的销售属性相关信息
        //this.saveSkuInfos(spuId,spuAllVo.getSkus());
        proxy.saveSkuInfos(spuId, spuAllVo.getSkus());

        // 4.远程调用商品优惠微服务存储商品优惠信息(saveSkuInfos方法中)
        // 4.1提取出所有的优惠信息
    }


    /**
     * 负责解析出数据作出相应的业务
     *
     * @param spuAllVo
     * @return
     */
    @Override
    public Long saveSpuBaseInfo(SpuAllSaveVo spuAllVo) {

        // 1.保存spu的基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuAllVo, spuInfoEntity);
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
            entity.setAttrValue(AppUtils.arrayToStringWithSeperator(valueSelected, ","));
            // 注入默认值
            entity.setAttrSort(0);
            entity.setQuickShow(1);
            entity.setSpuId(spuId);

            allSave.add(entity);
        }
        productAttrValueDao.insertBatch(allSave);
    }


    /**
     * 保存sku的所有详情
     *
     * @param spuId
     * @param skus
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveSkuInfos(Long spuId, List<SkuVo> skus) {
        // 0.查出spu信息：进方法的所以sku属于同一个spu下
        SpuInfoEntity spuInfoEntity = this.getById(spuId);

        List<SkuSaleInfoTo> tos = new ArrayList<>();
        Long id = spuInfoEntity.getId();
        // catalog_id  brand_id
        // 1.保存sku的info信息
        for (SkuVo skuVo : skus) {
            String[] images = skuVo.getImages();
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            if (images != null && images.length > 0) {
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }
            skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuSubtitle(skuVo.getSkuSubtitle());
            skuInfoEntity.setWeight(skuVo.getWeight());
            skuInfoEntity.setPrice(skuVo.getPrice());

            skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();


            // 2.保存sku的所有对应图片
            for (int i = 0; i < images.length; i++) {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setDefaultImg(i == 0 ? 1 : 0);
                skuImagesEntity.setImgUrl(images[i]);
                skuImagesEntity.setImgSort(0);
                skuImagesDao.insert(skuImagesEntity);
            }


            // 3.当前sku的所有销售属性 组合 保存起来
            List<SaleAttrVo> saleAttrs = skuVo.getSaleAttrs();
            for (SaleAttrVo saleAttr : saleAttrs) {
                // 查询当前属性的信息
                SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                entity.setAttrId(saleAttr.getAttrId());
                AttrEntity attrEntity = attrDao.selectById(saleAttr.getAttrId());
                entity.setAttrName(attrEntity.getAttrName());
                entity.setAttrSort(0);
                entity.setAttrValue(saleAttr.getAttrValue());
                entity.setSkuId(skuId);
                // 保存sku与销售属性的关联关系
                skuSaleAttrValueDao.insert(entity);
            }

            //-------------pms系统工作完成--------------

            //-------------以下由sms完成，保存每一个sku的相关数据--------------
            SkuSaleInfoTo skuSaleInfoTo = new SkuSaleInfoTo();
            BeanUtils.copyProperties(skuVo, skuSaleInfoTo);
            skuSaleInfoTo.setSkuId(skuId);

            tos.add(skuSaleInfoTo);
        }
        // for循环结束后,远程调用sms微服务，完成保存
        log.info("--- Pms send data to Sms ---{}", tos);
        smsSkuSaleInfoFeifnService.saveSkuSaleInfos(tos);
        log.info("--- Pms send data ");
    }

    @Override
    public void updateSpuStatus(Integer status, Long spuId) {
        // 1.修改数据库中上下架的状态

        // 2.上架：将商品需要检索的信息放在es中
        //   下架：将商品需要检索的信息从es中删除

        if (status == 1) {
            // 下架
            spuUp(spuId, status);
        } else {
            // 上架
            spuDown(spuId, status);
        }
    }

    /**
     * 上架商品
     *
     * @param spuId
     * @param status
     */
    private void spuUp(Long spuId, Integer status) {
        // 1.查出要使用的基本信息（品牌、分类 sku和spu是一致的）
        SpuInfoEntity spuInfoEntity = spuInfoDao.selectById(spuId);
        BrandEntity brandEntity = brandDao.selectById(spuInfoEntity.getBrandId());
        CategoryEntity category = categoryDao.selectById(spuInfoEntity.getCatalogId());


        // 2.上架：将商品需要检索的信息放在es中
        ArrayList<EsSkuVo> esSkuVos = new ArrayList<>();
        // 2.1查出当前需要上架的spu的所有sku信息
        List<SkuInfoEntity> skus = skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        List<Long> skuIds = new ArrayList<>();


        // 2.1.1远程调用库存微服务，查询spu下所有sku的库存信息
        skus.forEach(skuInfoEntity -> {
            Long skuId = skuInfoEntity.getSkuId();
            skuIds.add(skuId);
        });

        // 2.1.2远程检索所有sku的库存信息
        Resp<List<SkuStockVo>> infos = wmsFeignService.skuWareInfos(skuIds);
        List<SkuStockVo> skuStockVos = infos.getData();
        // 2.1.3查出当前spu所有可以供检索的属性 attr search_type字段表示是否需要被检索到（先挑出所有属性，再筛选出）
        List<ProductAttrValueEntity> spu_id = productAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        // 2.1.4过滤出可以被检索的
        ArrayList<Long> attrIds = new ArrayList<>();
        spu_id.forEach(productAttrValueEntity -> {
                    attrIds.add(productAttrValueEntity.getAttrId());
                }
        );
        // 过滤掉非筛选属性
        List<AttrEntity> list = attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrIds).eq("search_type", 1));
        // 2.1.5在spu_id过滤处list的所有数据
        // ArrayList<ProductAttrValueEntity> productAttrValueEntities = new ArrayList<>();
        List<EsSkuAttributeValue> esSkuAttributeValues = new ArrayList<>();
        list.forEach((item) -> {
            //当前能被检索的属性
            Long attrId = item.getAttrId();
            //拿到真正的值
            spu_id.forEach(s -> {
                if (item.getAttrId() == s.getAttrId()) {

                    // s为当前属性
                    EsSkuAttributeValue value = new EsSkuAttributeValue();
                    value.setId(s.getId());
                    value.setName(s.getAttrName());
                    value.setProductAttributeId(s.getAttrId());
                    value.setSpuId(spuId);
                    value.setValue(s.getAttrValue());

                    esSkuAttributeValues.add(value);

                    //productAttrValueEntities.add(s);
                }
            });
        });


        if (skus != null && skus.size() > 0) {
            // 2.2 构造所有需要保存在es中的sku信息
            skus.forEach(skuInfoEntity -> {
                EsSkuVo esSkuVo = skuInfoToEsSkuVo(skuInfoEntity, spuInfoEntity, brandEntity, category, skuStockVos, esSkuAttributeValues);
                esSkuVos.add(esSkuVo);
            });

            // 3.远程调用gulimall-search微服务对Es添加数据,远程端给予结果响应
            Resp<Object> resp = esFeignService.spuUp(esSkuVos);
            // 远程调用成功则修改本地数据库
            if (resp.getCode() == 0) {
                SpuInfoEntity entity = new SpuInfoEntity();
                entity.setId(spuId);
                entity.setPublishStatus(1);
                entity.setUodateTime(new Date());
                spuInfoDao.updateById(entity);
            }

            // 4.修改数据库中上下架的状态
        }
    }

    /**
     * 下架商品
     *
     * @param spuId
     * @param status
     */
    private void spuDown(Long spuId, Integer status) {
        // 1.修改数据库中上下架的状态

        // 2.下架：将商品需要检索的信息从es中删除
        ArrayList<EsSkuVo> esSkuVos = new ArrayList<>();
        // 3.远程调用gulimall-search微服务对Es删除数据
        Resp<Object> resp = esFeignService.spuDown(esSkuVos);
        if (resp.getCode() == 0) {
            SpuInfoEntity entity = new SpuInfoEntity();
            entity.setId(spuId);
            entity.setPublishStatus(0);
            entity.setUodateTime(new Date());
            spuInfoDao.updateById(entity);
        }
    }

    /**
     * 将SkuInfoEntity加工成EsSkuVo
     *
     * @param skuInfoEntity
     * @param spuInfoEntity
     * @param skuStockVos
     * @param productAttrValueEntities
     * @return
     */
    private EsSkuVo skuInfoToEsSkuVo(SkuInfoEntity skuInfoEntity, SpuInfoEntity spuInfoEntity, BrandEntity brandEntity, CategoryEntity category, List<SkuStockVo> skuStockVos, List<EsSkuAttributeValue> productAttrValueEntities) {

        EsSkuVo vo = new EsSkuVo();
        vo.setId(skuInfoEntity.getSkuId());
        vo.setBrandId(skuInfoEntity.getBrandId());
        // 品牌名
        if (brandEntity != null) {
            vo.setBrandName(brandEntity.getName());
        }
        // 搜索的标题
        vo.setName(skuInfoEntity.getSkuTitle());
        // sku的图片
        vo.setPic(skuInfoEntity.getSkuDefaultImg());
        // sku的价格
        vo.setPrice(skuInfoEntity.getPrice());
        // 所属分类的id
        vo.setProductCategoryId(skuInfoEntity.getCatalogId());
        // 所属分类的名字
        if (category != null) {
            vo.setProductCategoryName(category.getName());
        }
        vo.setSale(0);

        vo.setSort(0);
        // 保存库存
        skuStockVos.forEach(item -> {
            if (item.getSkuId() == skuInfoEntity.getSkuId()) {
                vo.setStock(item.getStock());
            }
        });
        vo.setAttrValueList(productAttrValueEntities);
        vo.setAttrValueList(null);

        return vo;
    }
}