package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.pms.annotation.GuliCache;
import com.atguigu.gulimall.pms.vo.CategoryWithChildrensVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.CategoryDao;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.pms.service.CategoryService;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<CategoryEntity> getCategoryByLevel(Integer level) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        if (level != 0) {
            wrapper.eq("cat_level", level);
        }
        return baseMapper.selectList(wrapper);
    }

    /**
     * @param catId
     * @return
     */
    @Override
    public List<CategoryEntity> getCategoryChildrensById(Integer catId) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_cid", catId);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 利用mybatis层的递归查询某id下的子分类及子子分类，并增加缓存
     * 利用AOP做缓存切面
     *
     * @param id
     * @return
     */
    @GuliCache(prefix = Constant.CACHE_CATELOG)
    @Override
    public List<CategoryWithChildrensVo> getCategoryChildrensAndSubsById(Integer id) {
        log.info("目标方法运行");
        List<CategoryWithChildrensVo> vos = categoryDao.selectCategoryChildrenWithChildrens(id);

        /**
         *  1.缓存穿透:null值缓存
         *  2.缓存雪崩:设置随机过期时间
         *  3.缓存击穿:分布式锁
         */
        // 先查缓存，缓存中有拿缓存的
//        String cache = redisTemplate.opsForValue().get(Constant.CACHE_CATELOG);
//        if (!StringUtils.isEmpty(cache)) {
//            log.info("菜单数据缓存命中......");
//            vos = JSON.parseArray(cache, CategoryWithChildrensVo.class);
//        } else {
//            log.info("菜单数据缓存未命中......查询数据库");
//            vos = categoryDao.selectCategoryChildrenWithChildrens(id);
//            // 数据库中查完放到缓存中
//            redisTemplate.opsForValue().set(Constant.CACHE_CATELOG, JSON.toJSONString(vos));
//        }

        return vos;
    }

}