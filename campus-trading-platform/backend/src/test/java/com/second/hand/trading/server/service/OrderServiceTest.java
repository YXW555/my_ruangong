package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.OrderModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderService 单元测试
 * 
 * 测试说明：
 * 1. 使用 @SpringBootTest 加载完整的 Spring 上下文
 * 2. 使用 @Transactional 确保测试后回滚，不影响数据库
 * 3. 使用 @ActiveProfiles("test") 使用测试配置（H2内存数据库）
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private IdleItemService idleItemService;

    /**
     * 测试创建订单功能
     * 注意：创建订单会下架商品，需要先创建一个上架的商品
     */
    @Test
    void testAddOrder() {
        // 先创建一个上架的商品
        IdleItemModel idleItem = new IdleItemModel();
        idleItem.setUserId(1L);
        idleItem.setIdleName("测试商品");
        idleItem.setIdlePrice(new BigDecimal("99.99"));
        idleItem.setIdleLabel(1);
        idleItem.setIdleStatus((byte) 1); // 上架状态
        idleItem.setReleaseTime(new Date());
        idleItem.setStock(1);
        idleItemService.addIdleItem(idleItem);
        
        // 准备订单数据
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(2L); // 买家ID
        orderModel.setIdleId(idleItem.getId());
        orderModel.setOrderNumber("TEST" + System.currentTimeMillis());
        orderModel.setCreateTime(new Date());
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte) 0);
        orderModel.setOrderPrice(new BigDecimal("99.99"));
        
        // 执行测试
        boolean result = orderService.addOrder(orderModel);
        
        // 验证结果
        assertTrue(result);
        assertNotNull(orderModel.getId());
    }

    /**
     * 测试获取订单信息功能
     */
    @Test
    void testGetOrder() {
        // 先创建一个订单
        IdleItemModel idleItem = new IdleItemModel();
        idleItem.setUserId(1L);
        idleItem.setIdleName("测试商品");
        idleItem.setIdlePrice(new BigDecimal("99.99"));
        idleItem.setIdleLabel(1);
        idleItem.setIdleStatus((byte) 1);
        idleItem.setReleaseTime(new Date());
        idleItem.setStock(1);
        idleItemService.addIdleItem(idleItem);
        
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(2L);
        orderModel.setIdleId(idleItem.getId());
        orderModel.setOrderNumber("TEST" + System.currentTimeMillis());
        orderModel.setCreateTime(new Date());
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte) 0);
        orderModel.setOrderPrice(new BigDecimal("99.99"));
        orderService.addOrder(orderModel);
        
        // 执行测试
        OrderModel result = orderService.getOrder(orderModel.getId());
        
        // 验证结果
        assertNotNull(result);
        assertEquals(orderModel.getId(), result.getId());
        assertEquals(orderModel.getOrderNumber(), result.getOrderNumber());
    }

    /**
     * 测试获取用户订单列表功能
     */
    @Test
    void testGetMyOrder() {
        // 先创建一个订单
        IdleItemModel idleItem = new IdleItemModel();
        idleItem.setUserId(1L);
        idleItem.setIdleName("测试商品");
        idleItem.setIdlePrice(new BigDecimal("99.99"));
        idleItem.setIdleLabel(1);
        idleItem.setIdleStatus((byte) 1);
        idleItem.setReleaseTime(new Date());
        idleItem.setStock(1);
        idleItemService.addIdleItem(idleItem);
        
        Long userId = 2L;
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setIdleId(idleItem.getId());
        orderModel.setOrderNumber("TEST" + System.currentTimeMillis());
        orderModel.setCreateTime(new Date());
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte) 0);
        orderModel.setOrderPrice(new BigDecimal("99.99"));
        orderService.addOrder(orderModel);
        
        // 执行测试
        List<OrderModel> result = orderService.getMyOrder(userId);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    /**
     * 测试更新订单功能
     */
    @Test
    void testUpdateOrder() {
        // 先创建一个订单
        IdleItemModel idleItem = new IdleItemModel();
        idleItem.setUserId(1L);
        idleItem.setIdleName("测试商品");
        idleItem.setIdlePrice(new BigDecimal("99.99"));
        idleItem.setIdleLabel(1);
        idleItem.setIdleStatus((byte) 1);
        idleItem.setReleaseTime(new Date());
        idleItem.setStock(1);
        idleItemService.addIdleItem(idleItem);
        
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(2L);
        orderModel.setIdleId(idleItem.getId());
        orderModel.setOrderNumber("TEST" + System.currentTimeMillis());
        orderModel.setCreateTime(new Date());
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte) 0);
        orderModel.setOrderPrice(new BigDecimal("99.99"));
        orderService.addOrder(orderModel);
        
        // 更新订单状态
        orderModel.setOrderStatus((byte) 1); // 已确认
        orderModel.setPaymentStatus((byte) 1); // 已支付
        orderModel.setPaymentTime(new Date());
        
        // 执行测试
        boolean result = orderService.updateOrder(orderModel);
        
        // 验证结果
        assertTrue(result);
        
        // 验证更新是否生效
        OrderModel updatedOrder = orderService.getOrder(orderModel.getId());
        assertEquals((byte) 1, updatedOrder.getOrderStatus());
        assertEquals((byte) 1, updatedOrder.getPaymentStatus());
    }

    /**
     * 测试获取用户卖出商品订单列表功能
     */
    @Test
    void testGetMySoldIdle() {
        // 先创建一个订单
        Long sellerId = 1L;
        IdleItemModel idleItem = new IdleItemModel();
        idleItem.setUserId(sellerId);
        idleItem.setIdleName("测试商品");
        idleItem.setIdlePrice(new BigDecimal("99.99"));
        idleItem.setIdleLabel(1);
        idleItem.setIdleStatus((byte) 1);
        idleItem.setReleaseTime(new Date());
        idleItem.setStock(1);
        idleItemService.addIdleItem(idleItem);
        
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(2L); // 买家ID
        orderModel.setIdleId(idleItem.getId());
        orderModel.setOrderNumber("TEST" + System.currentTimeMillis());
        orderModel.setCreateTime(new Date());
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte) 0);
        orderModel.setOrderPrice(new BigDecimal("99.99"));
        orderService.addOrder(orderModel);
        
        // 执行测试
        List<OrderModel> result = orderService.getMySoldIdle(sellerId);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    /**
     * 测试创建订单时商品必须为上架状态
     */
    @Test
    void testAddOrderWithNonActiveItem() {
        // 创建一个下架的商品
        IdleItemModel idleItem = new IdleItemModel();
        idleItem.setUserId(1L);
        idleItem.setIdleName("下架商品");
        idleItem.setIdlePrice(new BigDecimal("99.99"));
        idleItem.setIdleLabel(1);
        idleItem.setIdleStatus((byte) 0); // 下架状态
        idleItem.setReleaseTime(new Date());
        idleItem.setStock(1);
        idleItemService.addIdleItem(idleItem);
        
        // 准备订单数据
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(2L);
        orderModel.setIdleId(idleItem.getId());
        orderModel.setOrderNumber("TEST" + System.currentTimeMillis());
        orderModel.setCreateTime(new Date());
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte) 0);
        orderModel.setOrderPrice(new BigDecimal("99.99"));
        
        // 执行测试（应该失败，因为商品未上架）
        boolean result = orderService.addOrder(orderModel);
        
        // 验证结果（应该返回false）
        assertFalse(result);
    }
}

