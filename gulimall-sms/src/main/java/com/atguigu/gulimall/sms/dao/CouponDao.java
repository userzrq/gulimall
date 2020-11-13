package com.atguigu.gulimall.sms.dao;

import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 优惠券信息
 * 
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:26:20
 */
@Repository
public interface CouponDao extends BaseMapper<CouponEntity> {

    List<CouponEntity> selectCouponsBySpuId(@Param("spuId") Long spuId);
}
