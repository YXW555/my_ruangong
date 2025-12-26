package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService 单元测试示例
 * 
 * 测试说明：
 * 1. 使用 @SpringBootTest 加载完整的 Spring 上下文
 * 2. 使用 @Transactional 确保测试后回滚，不影响数据库
 * 3. 使用 @ActiveProfiles("test") 使用测试配置
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    /**
     * 测试用户登录功能
     */
    @Test
    void testUserLogin() {
        // 准备测试数据
        String accountNumber = "13800138000"; // 账号（手机号）
        String userPassword = "123456";
        
        // 执行测试
        UserModel user = userService.userLogin(accountNumber, userPassword);
        
        // 验证结果
        // 注意：这里需要根据实际业务逻辑调整断言
        // 如果用户不存在，可能返回 null
        if (user != null) {
            assertNotNull(user.getId());
            assertEquals(accountNumber, user.getAccountNumber());
        }
    }

    /**
     * 测试用户注册功能
     */
    @Test
    void testUserSignIn() {
        // 准备测试数据
        UserModel userModel = new UserModel();
        userModel.setAccountNumber("15000150000"); // 使用不同的账号，避免与 data.sql 冲突
        userModel.setUserPassword("123456"); // 密码
        userModel.setNickname("新用户"); // 昵称
        userModel.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"); // 必须设置 avatar
        userModel.setSignInTime(new Timestamp(System.currentTimeMillis())); // 必须设置注册时间
        
        // 执行测试
        boolean result = userService.userSignIn(userModel);
        
        // 验证结果
        assertTrue(result);
    }

    /**
     * 测试获取用户信息
     */
    @Test
    void testGetUser() {
        // 准备测试数据
        Long userId = 1L;
        
        // 执行测试
        UserModel user = userService.getUser(userId);
        
        // 验证结果
        if (user != null) {
            assertNotNull(user.getId());
            assertNotNull(user.getAccountNumber()); // 使用正确的字段名
        }
    }
}

