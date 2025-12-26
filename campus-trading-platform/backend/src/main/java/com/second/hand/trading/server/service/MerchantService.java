package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.OrderModel;

import java.util.List;
import java.util.Map;

/**
 * 经营性卖家服务接口
 */
public interface MerchantService {

    /**
     * 批量发布商品
     */
    boolean batchAddItems(Long userId, List<IdleItemModel> items);

    /**
     * 更新商品库存
     */
    boolean updateStock(Long idleId, Integer stock);

    /**
     * 获取店铺交易数据统计
     */
    Map<String, Object> getShopStatistics(Long userId);

    /**
     * 获取店铺所有商品
     */
    List<IdleItemModel> getShopItems(Long userId);

    /**
     * 获取店铺订单列表
     */
    List<OrderModel> getShopOrders(Long userId);
}

