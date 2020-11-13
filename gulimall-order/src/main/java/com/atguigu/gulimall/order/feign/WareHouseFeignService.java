package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.order.vo.ware.LockStockVo;
import com.atguigu.gulimall.order.vo.ware.SkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 10017
 */
@FeignClient("gulimall-wms")
public interface WareHouseFeignService {

    /**
     * 验证库存并锁库存
     *
     * @param skuLockVos
     * @return
     */
    @PostMapping("wms/waresku/checkAndLock")
    public Resp<LockStockVo> lockAndCheckStock(@RequestBody List<SkuLockVo> skuLockVos);
}
