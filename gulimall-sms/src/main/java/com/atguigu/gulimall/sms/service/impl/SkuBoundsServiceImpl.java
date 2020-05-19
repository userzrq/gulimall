package com.atguigu.gulimall.sms.service.impl;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.sms.dao.SkuFullReductionDao;
import com.atguigu.gulimall.sms.dao.SkuLadderDao;
import com.atguigu.gulimall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.sms.entity.SkuLadderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.sms.dao.SkuBoundsDao;
import com.atguigu.gulimall.sms.entity.SkuBoundsEntity;
import com.atguigu.gulimall.sms.service.SkuBoundsService;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuBoundsDao skuBoundsDao;

    @Autowired
    private SkuLadderDao skuLadderDao;

    @Autowired
    private SkuFullReductionDao skuFullReductionDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    // 保存SkuSaleInfoTo对象中商品的优惠信息
    @Override
    public void saveSkuAllSaleInfo(List<SkuSaleInfoTo> tos) {
        if(tos != null && tos.size() >0){
            for (SkuSaleInfoTo to : tos) {
                // 1.sku_bounds 积分信息的保存
                SkuBoundsEntity boundsEntity = new SkuBoundsEntity();
                Integer[] work = to.getWork();
                Integer i = 2^3*work[0]+2^2*work[1]+2^1*work[2]+2^0*work[3];
                // 设置状态位
                boundsEntity.setWork(i);
                // 具体优惠券的使用信息去枚举类中对应
                boundsEntity.setBuyBounds(to.getBuyBounds());
                boundsEntity.setGrowBounds(to.getGrowBounds());
                boundsEntity.setSkuId(to.getSkuId());
                // BeanUtils.copyProperties(to,boundsEntity);
                skuBoundsDao.insert(boundsEntity);

                // 2.sku_ladder 阶梯价格的保存
                SkuLadderEntity ladderEntity = new SkuLadderEntity();
                ladderEntity.setFullCount(to.getFullCount());
                ladderEntity.setDiscount(to.getDiscount());
                ladderEntity.setAddOther(to.getLadderAddOther());
                ladderEntity.setSkuId(to.getSkuId());
                // BeanUtils.copyProperties(to,ladderEntity);
                skuLadderDao.insert(ladderEntity);

                // 3.sku_full_reduction 满减信息的保存
                SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
                BeanUtils.copyProperties(to,fullReductionEntity);
                fullReductionEntity.setAddOther(to.getFullAddOther());
                skuFullReductionDao.insert(fullReductionEntity);
            }
        }
    }

}