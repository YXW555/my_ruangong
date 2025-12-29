package com.second.hand.trading.server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OrderController 集成测试
 * 
 * 测试说明：
 * 1. 使用 @AutoConfigureMockMvc 配置 MockMvc
 * 2. 测试 Controller 层的 HTTP 请求处理
 * 3. 验证返回的 JSON 格式和状态码
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试创建订单接口
     */
    @Test
    void testAddOrder() throws Exception {
        String requestBody = "{\n" +
                "  \"idleId\": 1,\n" +
                "  \"orderPrice\": 99.99,\n" +
                "  \"addressId\": 1\n" +
                "}";
        
        mockMvc.perform(post("/order/add")
                .cookie(new javax.servlet.http.Cookie("shUserId", "2"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试获取订单信息接口
     */
    @Test
    void testGetOrderInfo() throws Exception {
        mockMvc.perform(get("/order/info")
                .cookie(new javax.servlet.http.Cookie("shUserId", "1"))
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试更新订单接口
     */
    @Test
    void testUpdateOrder() throws Exception {
        String requestBody = "{\n" +
                "  \"id\": 1,\n" +
                "  \"orderStatus\": 1,\n" +
                "  \"paymentStatus\": 1\n" +
                "}";
        
        mockMvc.perform(post("/order/update")
                .cookie(new javax.servlet.http.Cookie("shUserId", "1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试获取我的订单列表接口
     */
    @Test
    void testGetMyOrder() throws Exception {
        mockMvc.perform(get("/order/my")
                .cookie(new javax.servlet.http.Cookie("shUserId", "1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试获取我卖出的商品订单接口
     */
    @Test
    void testGetMySoldIdle() throws Exception {
        mockMvc.perform(get("/order/mySoldIdle")
                .cookie(new javax.servlet.http.Cookie("shUserId", "1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试未登录时创建订单（应该失败）
     */
    @Test
    void testAddOrderWithoutLogin() throws Exception {
        String requestBody = "{\n" +
                "  \"idleId\": 1,\n" +
                "  \"orderPrice\": 99.99\n" +
                "}";
        
        mockMvc.perform(post("/order/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest()); // 应该返回400错误
    }
}

