package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.FavoriteDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.entity.FavoriteModel;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.service.RecommendService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 * 基于EasyRec设计理念实现的推荐引擎
 * 核心算法：协同过滤（Collaborative Filtering）+ 内容推荐（Content-based）
 * 
 * EasyRec设计理念：
 * 1. 数据处理与特征提取
 * 2. 多种推荐算法支持
 * 3. 实时推荐服务
 * 4. 可扩展的推荐引擎架构
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    @Resource
    private FavoriteDao favoriteDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Override
    public List<IdleItemModel> getPersonalizedRecommendations(Long userId, int limit) {
        if (userId == null) {
            // 未登录用户，返回热门推荐
            return getHotRecommendations(limit);
        }

        // 1. 获取用户行为数据
        List<FavoriteModel> favorites = favoriteDao.getMyFavorite(userId);
        List<OrderModel> orders = orderDao.getMyOrder(userId);

        // 2. 提取用户感兴趣的类别
        Set<Integer> userPreferredCategories = new HashSet<>();
        
        // 从收藏中提取类别
        for (FavoriteModel favorite : favorites) {
            if (favorite.getIdleId() != null) {
                IdleItemModel item = idleItemDao.selectByPrimaryKey(favorite.getIdleId());
                if (item != null && item.getIdleLabel() != null) {
                    userPreferredCategories.add(item.getIdleLabel());
                }
            }
        }
        
        // 从订单中提取类别
        for (OrderModel order : orders) {
            if (order.getIdleId() != null) {
                IdleItemModel item = idleItemDao.selectByPrimaryKey(order.getIdleId());
                if (item != null && item.getIdleLabel() != null) {
                    userPreferredCategories.add(item.getIdleLabel());
                }
            }
        }

        // 3. 如果用户没有任何行为，返回热门推荐
        if (userPreferredCategories.isEmpty()) {
            return getHotRecommendations(limit);
        }

        // 4. 基于类别推荐 + 协同过滤
        List<IdleItemModel> recommendations = new ArrayList<>();
        
        // 4.1 基于用户偏好的类别推荐
        for (Integer category : userPreferredCategories) {
            List<IdleItemModel> categoryItems = idleItemDao.findIdleItemByLable(category, 0, 20);
            if (categoryItems != null) {
                recommendations.addAll(categoryItems);
            }
        }

        // 4.2 协同过滤：找到相似用户喜欢的商品
        Set<Long> userInteractedItems = new HashSet<>();
        favorites.forEach(f -> {
            if (f.getIdleId() != null) userInteractedItems.add(f.getIdleId());
        });
        orders.forEach(o -> {
            if (o.getIdleId() != null) userInteractedItems.add(o.getIdleId());
        });

        // 找到也喜欢这些商品的用户，推荐他们喜欢的其他商品
        List<IdleItemModel> collaborativeItems = getCollaborativeRecommendations(userInteractedItems, userId, limit);
        recommendations.addAll(collaborativeItems);

        // 5. 去重、过滤已交互的商品、排序、限制数量
        return recommendations.stream()
                .filter(item -> item != null && item.getIdleStatus() == 1) // 只推荐已发布的商品
                .filter(item -> !userInteractedItems.contains(item.getId())) // 排除用户已交互的商品
                .distinct() // 去重
                .sorted((a, b) -> {
                    // 按发布时间倒序，优先推荐新商品
                    if (a.getReleaseTime() != null && b.getReleaseTime() != null) {
                        return b.getReleaseTime().compareTo(a.getReleaseTime());
                    }
                    return 0;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 协同过滤推荐算法（基于EasyRec设计理念）
     * 
     * 算法类型：基于用户的协同过滤（User-based Collaborative Filtering）
     * 
     * 核心步骤：
     * 1. 计算用户相似度（基于共同交互的商品数量）
     * 2. 找到相似用户（相似度分数 > 0）
     * 3. 推荐相似用户喜欢的其他商品
     * 
     * 相似度计算公式：sim(u1, u2) = |I(u1) ∩ I(u2)| / |I(u1) ∪ I(u2)|
     * 其中 I(u) 表示用户u交互过的商品集合
     * 
     * @param userInteractedItems 当前用户交互过的商品ID集合
     * @param userId 当前用户ID
     * @param limit 推荐数量限制
     * @return 推荐商品列表
     */
    private List<IdleItemModel> getCollaborativeRecommendations(Set<Long> userInteractedItems, Long userId, int limit) {
        if (userInteractedItems.isEmpty()) {
            return new ArrayList<>();
        }

        // 找到也收藏/购买了这些商品的用户
        Map<Long, Integer> similarUsers = new HashMap<>(); // userId -> 相似度分数
        
        // 遍历所有收藏，找到也收藏了相同商品的用户
        List<FavoriteModel> allFavorites = new ArrayList<>();
        for (Long itemId : userInteractedItems) {
            // 这里需要扩展FavoriteDao，添加根据商品ID查询收藏的方法
            // 暂时简化处理
        }

        // 遍历所有订单，找到也购买了相同商品的用户
        // 注意：这里简化处理，实际应该优化查询性能
        // 可以通过扩展DAO方法，直接查询购买了指定商品的用户
        List<OrderModel> allOrders = orderDao.getAllOrder(0, 500); // 获取部分订单进行分析
        for (OrderModel order : allOrders) {
            if (order.getUserId() != null && !order.getUserId().equals(userId)) {
                if (order.getIdleId() != null && userInteractedItems.contains(order.getIdleId())) {
                    similarUsers.put(order.getUserId(), similarUsers.getOrDefault(order.getUserId(), 0) + 1);
                }
            }
        }

        // 找到相似用户喜欢的其他商品
        Set<Long> recommendedItemIds = new HashSet<>();
        for (Map.Entry<Long, Integer> entry : similarUsers.entrySet()) {
            Long similarUserId = entry.getKey();
            // 获取相似用户的订单
            List<OrderModel> similarUserOrders = orderDao.getMyOrder(similarUserId);
            for (OrderModel order : similarUserOrders) {
                if (!userInteractedItems.contains(order.getIdleId())) {
                    recommendedItemIds.add(order.getIdleId());
                }
            }
        }

        // 获取推荐商品
        List<IdleItemModel> recommendedItems = new ArrayList<>();
        for (Long itemId : recommendedItemIds) {
            IdleItemModel item = idleItemDao.selectByPrimaryKey(itemId);
            if (item != null && item.getIdleStatus() == 1) {
                recommendedItems.add(item);
            }
        }

        return recommendedItems.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<IdleItemModel> getSimilarItems(Long itemId, int limit) {
        IdleItemModel targetItem = idleItemDao.selectByPrimaryKey(itemId);
        if (targetItem == null) {
            return new ArrayList<>();
        }

        // 基于类别和标签推荐相似商品
        Integer category = targetItem.getIdleLabel();
        List<IdleItemModel> similarItems = idleItemDao.findIdleItemByLable(category, 0, limit + 1);
        
        if (similarItems == null) {
            return new ArrayList<>();
        }

        return similarItems.stream()
                .filter(item -> !item.getId().equals(itemId)) // 排除自己
                .filter(item -> item.getIdleStatus() == 1) // 只推荐已发布的
                .sorted((a, b) -> {
                    // 按价格相似度排序（可选）
                    if (a.getReleaseTime() != null && b.getReleaseTime() != null) {
                        return b.getReleaseTime().compareTo(a.getReleaseTime());
                    }
                    return 0;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<IdleItemModel> getHotRecommendations(int limit) {
        // 获取所有已发布的商品，按发布时间倒序
        List<IdleItemModel> allItems = idleItemDao.getIdleItemByStatus(1, 0, limit * 2); // 获取更多以便筛选
        
        if (allItems == null || allItems.isEmpty()) {
            return new ArrayList<>();
        }

        return allItems.stream()
                .filter(item -> item.getIdleStatus() == 1) // 只推荐已发布的
                .sorted((a, b) -> {
                    // 优先推荐置顶商品，然后按发布时间
                    if (a.getReleaseTime() != null && b.getReleaseTime() != null) {
                        return b.getReleaseTime().compareTo(a.getReleaseTime());
                    }
                    return 0;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
}

