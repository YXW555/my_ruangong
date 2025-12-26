package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.service.MerchantService;
import com.second.hand.trading.server.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddItems(Long userId, List<IdleItemModel> items) {
        Date now = new Date();
        for (IdleItemModel item : items) {
            item.setUserId(userId);
            item.setReleaseTime(now);
            item.setIdleStatus((byte) 1); // 默认发布状态
            if (item.getStock() == null || item.getStock() <= 0) {
                item.setStock(1); // 默认库存为1
            }
            if (idleItemDao.insert(item) != 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean updateStock(Long idleId, Integer stock) {
        IdleItemModel item = new IdleItemModel();
        item.setId(idleId);
        item.setStock(stock);
        return idleItemDao.updateByPrimaryKeySelective(item) == 1;
    }

    @Override
    public Map<String, Object> getShopStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();

        // 获取店铺所有商品
        List<IdleItemModel> items = idleItemDao.getAllIdleItem(userId);
        int totalItems = items.size();
        int onSaleItems = 0;
        int totalStock = 0;
        for (IdleItemModel item : items) {
            if (item.getIdleStatus() == 1) {
                onSaleItems++;
            }
            if (item.getStock() != null) {
                totalStock += item.getStock();
            }
        }

        // 获取店铺所有订单
        List<OrderModel> orders = orderService.getMySoldIdle(userId);
        if (orders == null) {
            orders = new ArrayList<>();
        }
        int totalOrders = orders.size();
        int completedOrders = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalPendingRevenue = BigDecimal.ZERO;

        for (OrderModel order : orders) {
            if (order.getOrderStatus() == 3) { // 已完成
                completedOrders++;
                if (order.getOrderPrice() != null) {
                    totalRevenue = totalRevenue.add(order.getOrderPrice());
                }
            } else if (order.getOrderStatus() == 1 || order.getOrderStatus() == 2) { // 待发货或待收货
                if (order.getOrderPrice() != null) {
                    totalPendingRevenue = totalPendingRevenue.add(order.getOrderPrice());
                }
            }
        }

        statistics.put("totalItems", totalItems);
        statistics.put("onSaleItems", onSaleItems);
        statistics.put("totalStock", totalStock);
        statistics.put("totalOrders", totalOrders);
        statistics.put("completedOrders", completedOrders);
        statistics.put("totalRevenue", totalRevenue);
        statistics.put("totalPendingRevenue", totalPendingRevenue);

        return statistics;
    }

    @Override
    public List<IdleItemModel> getShopItems(Long userId) {
        return idleItemDao.getAllIdleItem(userId);
    }

    @Override
    public List<OrderModel> getShopOrders(Long userId) {
        List<OrderModel> orders = orderService.getMySoldIdle(userId);
        return orders != null ? orders : new ArrayList<>();
    }
}

