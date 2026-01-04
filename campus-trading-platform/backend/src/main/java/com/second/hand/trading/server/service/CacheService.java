package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.dto.PageVo;
import com.second.hand.trading.server.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 * 实现商品列表和详情的缓存
 */
@Service
public class CacheService {

    @Autowired
    private RedisUtil redisUtil;

    // 缓存key前缀
    private static final String PRODUCT_LIST_KEY = "product:list:";
    private static final String PRODUCT_LIST_BY_LABEL_KEY = "product:list:label:";
    private static final String PRODUCT_DETAIL_KEY = "product:detail:";
    
    // 缓存过期时间（秒）
    private static final long PRODUCT_LIST_EXPIRE = 300; // 5分钟
    private static final long PRODUCT_DETAIL_EXPIRE = 1800; // 30分钟

    /**
     * 获取商品列表（带缓存）
     */
    public PageVo<IdleItemModel> getProductList(String findValue, int page, int nums, 
                                                java.util.function.Supplier<PageVo<IdleItemModel>> supplier) {
        String cacheKey = PRODUCT_LIST_KEY + findValue + ":" + page + ":" + nums;
        
        // 先从缓存获取
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            try {
                // 将缓存对象转换为PageVo
                String jsonStr = JSON.toJSONString(cached);
                com.alibaba.fastjson.TypeReference<PageVo<IdleItemModel>> typeRef = 
                    new com.alibaba.fastjson.TypeReference<PageVo<IdleItemModel>>(){};
                return JSON.parseObject(jsonStr, typeRef.getType());
            } catch (Exception e) {
                // 缓存数据格式错误，清除缓存，重新查询
                redisUtil.delete(cacheKey);
            }
        }
        
        // 缓存不存在，从数据库查询
        PageVo<IdleItemModel> result = supplier.get();
        
        // 存入缓存
        if (result != null && result.getList() != null && !result.getList().isEmpty()) {
            redisUtil.set(cacheKey, result, PRODUCT_LIST_EXPIRE);
        }
        
        return result;
    }

    /**
     * 获取商品详情（带缓存）
     */
    public IdleItemModel getProductDetail(Long id, java.util.function.Supplier<IdleItemModel> supplier) {
        String cacheKey = PRODUCT_DETAIL_KEY + id;
        
        // 先从缓存获取
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            try {
                return JSON.parseObject(JSON.toJSONString(cached), IdleItemModel.class);
            } catch (Exception e) {
                // 缓存数据格式错误，清除缓存，重新查询
                redisUtil.delete(cacheKey);
            }
        }
        
        // 缓存不存在，从数据库查询
        IdleItemModel result = supplier.get();
        
        // 存入缓存
        if (result != null) {
            redisUtil.set(cacheKey, result, PRODUCT_DETAIL_EXPIRE);
        }
        
        return result;
    }

    /**
     * 删除商品缓存（商品更新或删除时调用）
     */
    public void evictProductCache(Long productId) {
        // 删除详情缓存
        redisUtil.delete(PRODUCT_DETAIL_KEY + productId);
        
        // 删除所有列表缓存（全部失效）
        evictAllProductListCache();
    }

    /**
     * 获取分类商品列表（带缓存）
     */
    public PageVo<IdleItemModel> getProductListByLabel(int idleLabel, int page, int nums, 
                                                        java.util.function.Supplier<PageVo<IdleItemModel>> supplier) {
        String cacheKey = PRODUCT_LIST_BY_LABEL_KEY + idleLabel + ":" + page + ":" + nums;
        
        // 先从缓存获取
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            try {
                String jsonStr = JSON.toJSONString(cached);
                com.alibaba.fastjson.TypeReference<PageVo<IdleItemModel>> typeRef = 
                    new com.alibaba.fastjson.TypeReference<PageVo<IdleItemModel>>(){};
                return JSON.parseObject(jsonStr, typeRef.getType());
            } catch (Exception e) {
                redisUtil.delete(cacheKey);
            }
        }
        
        // 缓存不存在，从数据库查询
        PageVo<IdleItemModel> result = supplier.get();
        
        // 存入缓存
        if (result != null && result.getList() != null && !result.getList().isEmpty()) {
            redisUtil.set(cacheKey, result, PRODUCT_LIST_EXPIRE);
        }
        
        return result;
    }

    /**
     * 删除所有商品列表缓存
     */
    public void evictAllProductListCache() {
        // 使用前缀匹配删除所有商品列表相关的缓存
        redisUtil.deleteByPrefix(PRODUCT_LIST_KEY);
        redisUtil.deleteByPrefix(PRODUCT_LIST_BY_LABEL_KEY);
    }
}

