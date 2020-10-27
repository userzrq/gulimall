package com.atguigu.gulimall.wms.vo;

import lombok.Data;

import java.util.List;

/**
 * @author userzrq
 */
@Data
public class LockStockVo {

    private List<SkuLock> locks;

    /**
     * 本次锁定的 最终结果 是成功了还是失败了，用于直接判断
     */
    private Boolean locked;

    /**
     * 如果有拆单的逻辑，订单里面有很多来源于不同仓库的商品
     * 以仓库发货单位进行拆分
     */
}

