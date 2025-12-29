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
 * IdleItemController 集成测试
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
class IdleItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试发布商品接口
     */
    @Test
    void testAddIdleItem() throws Exception {
        String requestBody = "{\n" +
                "  \"idleName\": \"测试商品\",\n" +
                "  \"idlePrice\": 99.99,\n" +
                "  \"idleLabel\": 1,\n" +
                "  \"idleDescribe\": \"这是一个测试商品\",\n" +
                "  \"stock\": 1\n" +
                "}";
        
        mockMvc.perform(post("/idle/add")
                .cookie(new javax.servlet.http.Cookie("shUserId", "1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试获取商品信息接口
     */
    @Test
    void testGetIdleItem() throws Exception {
        // 注意：这个测试需要先有一个商品ID，可能需要先创建商品
        // 这里假设ID为1的商品存在（根据测试数据）
        mockMvc.perform(get("/idle/info")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试获取用户所有商品接口
     */
    @Test
    void testGetAllIdleItem() throws Exception {
        mockMvc.perform(get("/idle/all")
                .cookie(new javax.servlet.http.Cookie("shUserId", "1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试搜索商品接口
     */
    @Test
    void testFindIdleItem() throws Exception {
        mockMvc.perform(get("/idle/find")
                .param("findValue", "测试")
                .param("page", "1")
                .param("nums", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试按分类获取商品接口
     */
    @Test
    void testFindIdleItemByLabel() throws Exception {
        mockMvc.perform(get("/idle/findByLabel")
                .param("idleLabel", "1")
                .param("page", "1")
                .param("nums", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists());
    }

    /**
     * 测试未登录时发布商品（应该失败）
     */
    @Test
    void testAddIdleItemWithoutLogin() throws Exception {
        String requestBody = "{\n" +
                "  \"idleName\": \"测试商品\",\n" +
                "  \"idlePrice\": 99.99,\n" +
                "  \"idleLabel\": 1\n" +
                "}";
        
        mockMvc.perform(post("/idle/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest()); // 应该返回400错误
    }
}

