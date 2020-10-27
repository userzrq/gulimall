package com.atguigu.gulimall.wms.dao;

import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品库存
 *
 * @author userzrq
 * @email userzrq@126.com
 * @date 2020-05-18 10:36:15
 */
@Mapper
@Repository
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    /**
     * 检查商品未锁定库存量
     * @param skuLockVo
     * @return
     */
    Long ckeckStock(SkuLockVo skuLockVo);

    /**
     * 查询所有能减掉库存的仓库
     * @param skuLockVo
     * @return
     */
    List<WareSkuEntity> getAllWareCanLocked(SkuLockVo skuLockVo);

    /**
     *
     * @param skuLockVo
     * @param id    仓库id
     * @return
     */
    long lockSku(@Param("sku") SkuLockVo skuLockVo,
                 @Param("wareId") Long id);
}
