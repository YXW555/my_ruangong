package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.RatingModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RatingDao {

    /**
     * 插入一条评价
     */
    int insert(RatingModel record);

    /**
     * 根据订单ID查询评价
     */
    RatingModel selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询卖家的所有评价
     */
    List<RatingModel> selectBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 查询买家的所有评价
     */
    List<RatingModel> selectByBuyerId(@Param("buyerId") Long buyerId);

    /**
     * 计算卖家的平均评分
     */
    Double getAverageRating(@Param("sellerId") Long sellerId);

    /**
     * 统计卖家的评价数量
     */
    Integer countBySellerId(@Param("sellerId") Long sellerId);
}

