package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.dto.PageVo;
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
 * IdleItemService 单元测试
 * 
 * 测试说明：
 * 1. 使用 @SpringBootTest 加载完整的 Spring 上下文
 * 2. 使用 @Transactional 确保测试后回滚，不影响数据库
 * 3. 使用 @ActiveProfiles("test") 使用测试配置（H2内存数据库）
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IdleItemServiceTest {

    @Autowired
    private IdleItemService idleItemService;

    /**
     * 测试发布闲置商品功能
     */
    @Test
    void testAddIdleItem() {
        // 准备测试数据
        IdleItemModel idleItemModel = new IdleItemModel();
        idleItemModel.setUserId(1L);
        idleItemModel.setIdleName("测试商品");
        idleItemModel.setIdlePrice(new BigDecimal("99.99"));
        idleItemModel.setIdleLabel(1);
        idleItemModel.setIdleStatus((byte) 1);
        idleItemModel.setReleaseTime(new Date());
        idleItemModel.setStock(1);
        idleItemModel.setIdleDetails("这是一个测试商品");
        idleItemModel.setPictureList("[]");
        
        // 执行测试
        boolean result = idleItemService.addIdleItem(idleItemModel);
        
        // 验证结果
        assertTrue(result);
        assertNotNull(idleItemModel.getId());
    }

    /**
     * 测试获取商品信息功能
     */
    @Test
    void testGetIdleItem() {
        // 先创建一个商品
        IdleItemModel newItem = new IdleItemModel();
        newItem.setUserId(1L);
        newItem.setIdleName("测试商品");
        newItem.setIdlePrice(new BigDecimal("99.99"));
        newItem.setIdleLabel(1);
        newItem.setIdleStatus((byte) 1);
        newItem.setReleaseTime(new Date());
        newItem.setStock(1);
        newItem.setIdleDetails("这是一个测试商品");
        newItem.setPictureList("[]");
        idleItemService.addIdleItem(newItem);
        
        // 执行测试
        IdleItemModel result = idleItemService.getIdleItem(newItem.getId());
        
        // 验证结果
        assertNotNull(result);
        assertEquals(newItem.getId(), result.getId());
        assertEquals("测试商品", result.getIdleName());
    }

    /**
     * 测试获取用户所有商品功能
     */
    @Test
    void testGetAllIdelItem() {
        // 准备测试数据
        Long userId = 1L;
        
        // 先创建几个商品
        IdleItemModel item1 = new IdleItemModel();
        item1.setUserId(userId);
        item1.setIdleName("商品1");
        item1.setIdlePrice(new BigDecimal("10.00"));
        item1.setIdleLabel(1);
        item1.setIdleStatus((byte) 1);
        item1.setReleaseTime(new Date());
        item1.setStock(1);
        item1.setIdleDetails("详情占位");
        item1.setPictureList("[]");
        idleItemService.addIdleItem(item1);
        
        IdleItemModel item2 = new IdleItemModel();
        item2.setUserId(userId);
        item2.setIdleName("商品2");
        item2.setIdlePrice(new BigDecimal("20.00"));
        item2.setIdleLabel(1);
        item2.setIdleStatus((byte) 1);
        item2.setReleaseTime(new Date());
        item2.setStock(1);
        item2.setIdleDetails("详情占位");
        item2.setPictureList("[]");
        idleItemService.addIdleItem(item2);
        
        // 执行测试
        List<IdleItemModel> result = idleItemService.getAllIdelItem(userId);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    /**
     * 测试搜索商品功能
     */
    @Test
    void testFindIdleItem() {
        // 先创建一个商品
        IdleItemModel item = new IdleItemModel();
        item.setUserId(1L);
        item.setIdleName("测试搜索商品");
        item.setIdlePrice(new BigDecimal("99.99"));
        item.setIdleLabel(1);
        item.setIdleStatus((byte) 1);
        item.setReleaseTime(new Date());
        item.setStock(1);
        item.setIdleDetails("详情占位");
        item.setPictureList("[]");
        idleItemService.addIdleItem(item);
        
        // 执行测试
        PageVo<IdleItemModel> result = idleItemService.findIdleItem("测试", 1, 10);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getList());
    }

    /**
     * 测试按分类获取商品功能
     */
    @Test
    void testFindIdleItemByLable() {
        // 先创建一个商品
        IdleItemModel item = new IdleItemModel();
        item.setUserId(1L);
        item.setIdleName("分类测试商品");
        item.setIdlePrice(new BigDecimal("99.99"));
        item.setIdleLabel(1); // 分类ID为1
        item.setIdleStatus((byte) 1);
        item.setReleaseTime(new Date());
        item.setStock(1);
        idleItemService.addIdleItem(item);
        
        // 执行测试
        PageVo<IdleItemModel> result = idleItemService.findIdleItemByLable(1, 1, 10);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getList());
    }

    /**
     * 测试更新商品功能
     */
    @Test
    void testUpdateIdleItem() {
        // 先创建一个商品
        IdleItemModel item = new IdleItemModel();
        item.setUserId(1L);
        item.setIdleName("原始商品名");
        item.setIdlePrice(new BigDecimal("99.99"));
        item.setIdleLabel(1);
        item.setIdleStatus((byte) 1);
        item.setReleaseTime(new Date());
        item.setStock(1);
        idleItemService.addIdleItem(item);
        
        // 更新商品信息
        item.setIdleName("更新后的商品名");
        item.setIdlePrice(new BigDecimal("199.99"));
        
        // 执行测试
        boolean result = idleItemService.updateIdleItem(item);
        
        // 验证结果
        assertTrue(result);
        
        // 验证更新是否生效
        IdleItemModel updatedItem = idleItemService.getIdleItem(item.getId());
        assertEquals("更新后的商品名", updatedItem.getIdleName());
        assertEquals(new BigDecimal("199.99"), updatedItem.getIdlePrice());
    }

    /**
     * 测试发布商品时库存默认为1
     */
    @Test
    void testAddIdleItemWithNullStock() {
        // 准备测试数据（不设置库存）
        IdleItemModel idleItemModel = new IdleItemModel();
        idleItemModel.setUserId(1L);
        idleItemModel.setIdleName("无库存商品");
        idleItemModel.setIdlePrice(new BigDecimal("99.99"));
        idleItemModel.setIdleLabel(1);
        idleItemModel.setIdleStatus((byte) 1);
        idleItemModel.setReleaseTime(new Date());
        // 不设置stock，应该默认为1
        
        // 执行测试
        boolean result = idleItemService.addIdleItem(idleItemModel);
        
        // 验证结果
        assertTrue(result);
        // 验证库存被设置为1
        IdleItemModel savedItem = idleItemService.getIdleItem(idleItemModel.getId());
        assertNotNull(savedItem);
        assertEquals(1, savedItem.getStock());
    }
}

