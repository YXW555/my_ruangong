package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.AfterSaleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AfterSaleDao {
    /**
     * 插入售后申请
     */
    int insert(AfterSaleModel record);

    /**
     * 根据主键查询
     */
    AfterSaleModel selectByPrimaryKey(Long id);

    /**
     * 根据订单ID查询售后申请
     */
    AfterSaleModel selectByOrderId(Long orderId);

    /**
     * 查询买家的售后申请列表
     */
    List<AfterSaleModel> selectByBuyerId(Long buyerId);

    /**
     * 查询卖家的待审核售后申请列表
     */
    List<AfterSaleModel> selectPendingBySellerId(Long sellerId);

    /**
     * 查询管理员待审核的售后申请列表（状态为3或4）
     */
    List<AfterSaleModel> selectAdminPending();

    /**
     * 更新售后申请信息
     */
    int updateByPrimaryKeySelective(AfterSaleModel record);
}

