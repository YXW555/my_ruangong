package com.second.hand.trading.server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 集成测试示例
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper 暂时未使用，如需要可以取消注释
    // @Autowired
    // private ObjectMapper objectMapper;

    /**
     * 测试用户登录接口
     */
    @Test
    void testUserLogin() throws Exception {
        mockMvc.perform(post("/user/login")
                .param("accountNumber", "13800138000") // 使用正确的参数名
                .param("userPassword", "123456") // 使用正确的参数名
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists()); // 使用正确的字段名
    }

    /**
     * 测试用户注册接口
     */
    @Test
    void testUserSignIn() throws Exception {
        // 使用正确的字段名，使用不同的账号避免唯一约束冲突，必须包含 avatar
        String requestBody = "{\"accountNumber\":\"15000150000\",\"userPassword\":\"123456\",\"nickname\":\"新用户\",\"avatar\":\"https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png\"}";
        
        mockMvc.perform(post("/user/sign-in") // 使用正确的路径
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").exists()); // 使用正确的字段名
    }
}

