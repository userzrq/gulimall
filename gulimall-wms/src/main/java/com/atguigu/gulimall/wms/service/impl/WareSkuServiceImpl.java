package com.atguigu.gulimall.wms.service.impl;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.wms.vo.LockStockVo;
import com.atguigu.gulimall.wms.vo.SkuLock;
import com.atguigu.gulimall.wms.vo.SkuLockVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.wms.dao.WareSkuDao;
import com.atguigu.gulimall.wms.entity.WareSkuEntity;
import com.atguigu.gulimall.wms.service.WareSkuService;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author 10017
 */
@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    RedissonClient redisson;

    @Autowired
    private WareSkuDao wareSkuDao;

//    @Autowired
//    Executor executor;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    PlatformTransactionManager transactionManager;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    /**
     * @param skuLockVos 购物车中被勾选的，需要购买->查库存的skuLockVo(skuId,num)集合
     * @return
     */
    @Override
    public LockStockVo lockAndCheckStock(List<SkuLockVo> skuLockVos) {

        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        List<SkuLock> skuLocks = new ArrayList<>();
        CompletableFuture[] futures = new CompletableFuture[skuLockVos.size()];
        /**
         * ForkJoinPool
         * 核心：
         * 1.基于分布式锁; stock:locked:1  stock:locked:10  stock:locked:100
         * 2.基于数据库的乐观锁（但分布式项目一个服务会复制多份，每个进行修改的数据库也都不相同(双主热备)，各自维护各自表中的version字段就会出问题）
         *      update wms_ware_sku set stock_locked=stock_locked+5(5是原来的值),version = version + 1 where sku_id = ? and version = ?
         *      (乐观锁：要带对版本才能执行，版本没带对时，会快速返回失败)
         *
         * 要控制锁的粒度，不能喝对所有商品一概加锁
         * 粒度越细，并发越高
         */
        if (skuLockVos != null && skuLockVos.size() > 0) {
            log.info("需要锁定库存的商品有【{}】种...准备加锁...", skuLockVos.size());
            int i = 0;
            for (SkuLockVo skuLockVo : skuLockVos) {
                CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                    try {
                        log.info("锁库存开始..." + skuLockVo);
                        SkuLock skuLock = lockSku(skuLockVo);
                        log.info("锁库存结束..." + skuLockVo);
                        skuLocks.add(skuLock);
                        if (skuLock.getSuccess() == false) {
                            flag.set(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                futures[i] = async;
                i++;
            }
        }

        /**
         * 异步多线程锁库存，有一个锁不住，其他商品的锁定也要回滚
         * 1)、异步 @Transactional管不了不同线程
         * 2)、执行锁库存的sql并不会报错或出异常，每一个商品单独看自己能否锁住库存（手动判断回滚条件）
         * 3)、一个商品锁不住库存，就提示该商品库存不足，需要重新下单
         * 4)、不能使用最终一致性，应该使用强一致性，因为会影响实时库存
         */

        LockStockVo lockStockVo = new LockStockVo();
        CompletableFuture.allOf(futures);
        lockStockVo.setLocks(skuLocks);
        lockStockVo.setLocked(flag.get());
        /**
         * 待异步线程全部完成后,数组不允许带泛型，会导致转型失败
         */

        if (flag.get()) {
            // 库存都锁住了
            // 将锁库存的信息发送到消息队列，40min还没被消费就过期
            rabbitTemplate.convertAndSend("skuStockCreateExchange","create.skuStock",skuLocks);

        }


        return lockStockVo;
    }


    /**
     * 锁库存
     *
     * @param skuLockVo
     * @return
     */
    public SkuLock lockSku(SkuLockVo skuLockVo) throws InterruptedException {
        SkuLock skuLock = new SkuLock();
        // 1.检查总库存够不够锁的，不够就没必要锁了（先判断总量够不够，然后再找具体仓库）
        Long count = wareSkuDao.ckeckStock(skuLockVo);

        // 在总量满足的前提下
        if (count >= skuLockVo.getNum()) {
            // 寻找能够满足减库存的仓库（感觉可以用递归，因为可能一个仓库的总量不够减，但是总量是够的，总量够了，然后按可锁量排序，从多减到少）

            RLock lock = redisson.getLock(Constant.STOCK_LOCKED + skuLockVo.getSkuId());
            // 分布式锁尝试加锁
            boolean b = lock.tryLock(1, 1, TimeUnit.MINUTES);
            if (b) {
                List<WareSkuEntity> wareSkuEntities = wareSkuDao.getAllWareCanLocked(skuLockVo);
                // 判断仓库是否有足够够锁的库存数量
                if (wareSkuEntities != null && wareSkuEntities.size() > 0) {
                    WareSkuEntity wareSkuEntity = wareSkuEntities.get(0);
                    // 更新sql，返回影响行数
                    long i = wareSkuDao.lockSku(skuLockVo, wareSkuEntity.getWareId());
                    if (i > 0) {
                        skuLock.setSuccess(true);
                        skuLock.setSkuId(skuLockVo.getSkuId());
                        skuLock.setWareId(wareSkuEntity.getWareId());
                        // 锁住的数量，传进来的都锁住了
                        skuLock.setLocked(skuLockVo.getNum());
                    }
                } else {
                    log.warn("仓库库存有异常");
                }
            } else {
                // 加锁失败
                log.info("当前服务器压力大，请稍后再试");
            }
        } else {
            skuLock.setLocked(0);
            skuLock.setSkuId(skuLock.getSkuId());
            skuLock.setSuccess(false);
        }

        return skuLock;
    }

}