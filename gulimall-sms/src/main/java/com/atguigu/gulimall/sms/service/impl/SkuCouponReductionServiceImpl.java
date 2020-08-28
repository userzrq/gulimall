package com.atguigu.gulimall.sms.service.impl;

import com.atguigu.gulimall.sms.dao.CouponDao;
import com.atguigu.gulimall.sms.dao.CouponSpuRelationDao;
import com.atguigu.gulimall.sms.dao.SkuFullReductionDao;
import com.atguigu.gulimall.sms.dao.SkuLadderDao;
import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.atguigu.gulimall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.sms.entity.SkuLadderEntity;
import com.atguigu.gulimall.sms.feign.SpuFeignService;
import com.atguigu.gulimall.sms.service.SkuCouponReductionService;
import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.atguigu.gulimall.sms.to.SkuInfoTo;
import com.atguigu.gulimall.sms.to.SkuReductionTo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SkuCouponReductionServiceImpl implements SkuCouponReductionService {

    @Autowired
    CouponSpuRelationDao couponSpuRelationDao;

    @Autowired
    CouponDao couponDao;

    @Autowired
    SpuFeignService spuFeignService;

    @Autowired
    SkuLadderDao skuLadderDao;

    @Autowired
    SkuFullReductionDao skuFullReductionDao;

    @Override
    public List<SkuCouponTo> getCoupons(Long skuId) {
        List<SkuCouponTo> tos = new ArrayList<>();
        SkuInfoTo skuInfoTo = spuFeignService.info(skuId).getData();
        if (!Objects.isNull(skuInfoTo)) {
            Long spuId = skuInfoTo.getSpuId();
            // 优惠券信息和spuId有关联关系
            List<CouponEntity> entities = couponDao.selectCouponsBySpuId(spuId);

            if (!Objects.isNull(entities) && entities.size() > 0) {
                for (CouponEntity entity : entities) {
                    SkuCouponTo skuCouponTo = new SkuCouponTo();
                    skuCouponTo.setAmount(entity.getAmount());
                    skuCouponTo.setCouponId(entity.getId());
                    skuCouponTo.setDesc(entity.getCouponName());
                    skuCouponTo.setSkuId(skuId);
                    tos.add(skuCouponTo);
                }
            }
        }
        return tos;
    }

    @Override
    public List<SkuReductionTo> getReduction(Long skuId) {

        // 查阶梯价格
        List<SkuLadderEntity> ladderEntities = skuLadderDao.selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));

        List<SkuFullReductionEntity> fullReductionEntities = skuFullReductionDao.selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));

        List<SkuReductionTo> tos = new ArrayList<>();

        ladderEntities.forEach((ladderEntity) -> {
            SkuReductionTo to = new SkuReductionTo();
            BeanUtils.copyProperties(ladderEntity, to);
            to.setDesc("满" + ladderEntity.getFullCount() + "件，享受" + ladderEntity.getDiscount() + "折优惠");
            // 0 - 打折
            to.setType(0);
            tos.add(to);
        });

        fullReductionEntities.forEach((reductionEntity) -> {
            SkuReductionTo to = new SkuReductionTo();
            BeanUtils.copyProperties(reductionEntity, to);
            to.setDesc("消费满" + reductionEntity.getFullPrice() + "元，减" + reductionEntity.getReducePrice() + "员");
            // 1 - 满减
            to.setType(1);
            tos.add(to);
        });

        return tos;
    }
}
