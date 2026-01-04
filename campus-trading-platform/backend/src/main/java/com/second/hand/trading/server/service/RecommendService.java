package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.IdleItemModel;

import java.util.List;

/**
 * 推荐服务接口
 * 基于协同过滤算法的个性化推荐
 */
public interface RecommendService {
    /**
     * 获取用户个性化推荐商品
     * 基于用户行为（收藏、购买、浏览）进行协同过滤推荐
     * 
     * @param userId 用户ID（可为null，未登录用户返回热门推荐）
     * @param limit 推荐数量限制
     * @return 推荐商品列表
     */
    List<IdleItemModel> getPersonalizedRecommendations(Long userId, int limit);

    /**
     * 基于商品相似度推荐
     * 如果用户浏览/收藏/购买了某个商品，推荐相似的商品
     * 
     * @param itemId 商品ID
     * @param limit 推荐数量
     * @return 相似商品列表
     */
    List<IdleItemModel> getSimilarItems(Long itemId, int limit);

    /**
     * 获取热门推荐（未登录用户或新用户）
     * 
     * @param limit 推荐数量
     * @return 热门商品列表
     */
    List<IdleItemModel> getHotRecommendations(int limit);
}

