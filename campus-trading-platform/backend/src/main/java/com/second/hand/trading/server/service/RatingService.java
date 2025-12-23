package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.RatingModel;

import java.util.List;

/**
 * 评价服务接口
 */
public interface RatingService {

    /**
     * 添加评价
     */
    boolean addRating(RatingModel ratingModel);

    /**
     * 根据订单ID查询评价
     */
    RatingModel getRatingByOrderId(Long orderId);

    /**
     * 查询卖家的所有评价
     */
    List<RatingModel> getRatingsBySellerId(Long sellerId);

    /**
     * 查询买家的所有评价
     */
    List<RatingModel> getRatingsByBuyerId(Long buyerId);

    /**
     * 获取卖家的平均评分
     */
    Double getAverageRating(Long sellerId);

    /**
     * 获取卖家的评价数量
     */
    Integer getRatingCount(Long sellerId);
}

