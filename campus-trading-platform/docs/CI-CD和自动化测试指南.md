# CI/CD 和自动化测试完整指南

## 一、CI/CD 概述

### 什么是 CI/CD？

- **CI (Continuous Integration)**：持续集成
  - 代码提交后自动构建、测试
  - 及早发现集成问题
  
- **CD (Continuous Deployment)**：持续部署
  - 自动部署到生产环境
  - 减少人工操作，提高效率

### CI/CD 流程

```
代码提交 → 自动构建 → 自动测试 → 自动部署 → 生产环境
   ↓          ↓          ↓          ↓
 Git Push   Maven     单元测试    Docker
           Build      集成测试    部署
```

---

## 二、测试完整流程

### 测试金字塔

```
        /\
       /  \      E2E测试（端到端测试）
      /____\     少量，覆盖关键流程
     /      \    
    /________\   集成测试
   /          \  中等数量，测试模块间交互
  /____________\ 
 /              \ 单元测试
/________________\ 大量，测试单个方法/类
```

### 1. 单元测试（Unit Test）

**目的**：测试单个方法或类的功能

**范围**：
- Service 层的业务逻辑
- Util 工具类
- 数据验证逻辑

**工具**：
- JUnit 5
- Mockito（模拟依赖）

**示例**：
```java
// 测试 UserService 的登录功能
@Test
public void testUserLogin() {
    // 准备测试数据
    String username = "test";
    String password = "123456";
    
    // 执行测试
    UserModel user = userService.userLogin(username, password);
    
    // 验证结果
    assertNotNull(user);
    assertEquals(username, user.getUsername());
}
```

### 2. 集成测试（Integration Test）

**目的**：测试多个组件之间的交互

**范围**：
- Controller + Service + Dao 的完整流程
- 数据库操作
- Redis 缓存

**工具**：
- Spring Boot Test
- TestContainers（测试数据库）

**示例**：
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testUserLogin() throws Exception {
        mockMvc.perform(post("/user/login")
                .param("username", "test")
                .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

### 3. 端到端测试（E2E Test）

**目的**：测试完整的用户流程

**范围**：
- 用户注册 → 登录 → 发布商品 → 下单 → 支付
- 前端 + 后端的完整交互

**工具**：
- Cypress（前端 E2E 测试）
- Selenium（浏览器自动化）

**示例**：
```javascript
// Cypress 测试示例
describe('用户购买流程', () => {
  it('应该能够完成购买', () => {
    cy.visit('/login')
    cy.get('#username').type('test')
    cy.get('#password').type('123456')
    cy.get('button').click()
    
    cy.visit('/product/1')
    cy.get('.buy-button').click()
    cy.get('.confirm-button').click()
    
    cy.url().should('include', '/order')
  })
})
```

---

## 三、CI/CD 实现方案

### 方案一：GitHub Actions（推荐）

**优点**：
- ✅ 免费（公开仓库）
- ✅ 与 GitHub 集成
- ✅ 配置简单

### 方案二：GitLab CI/CD

**优点**：
- ✅ 功能强大
- ✅ 私有仓库免费
- ✅ 内置 Docker 支持

### 方案三：Jenkins

**优点**：
- ✅ 功能最全面
- ✅ 插件丰富
- ❌ 需要自己搭建服务器

---

## 四、GitHub Actions 实现

### 1. 创建 CI/CD 配置文件

在项目根目录创建 `.github/workflows/ci-cd.yml`

### 2. 工作流程

```
触发条件（Push/PR）
    ↓
构建后端（Maven）
    ↓
运行单元测试
    ↓
构建前端（npm build）
    ↓
运行前端测试（可选）
    ↓
构建 Docker 镜像
    ↓
部署到服务器
```

---

## 五、测试覆盖率

### 目标覆盖率

- **单元测试**：70%+
- **集成测试**：50%+
- **E2E 测试**：关键流程 100%

### 工具

- **JaCoCo**：Java 代码覆盖率
- **Istanbul**：JavaScript 代码覆盖率

---

## 六、部署策略

### 1. 蓝绿部署

- 两套环境（蓝/绿）
- 切换流量，零停机

### 2. 滚动部署

- 逐步替换实例
- 适合多实例场景

### 3. 金丝雀部署

- 先部署到少量实例
- 验证后全量部署

---

## 七、监控和告警

### 监控指标

- 应用性能（响应时间、错误率）
- 服务器资源（CPU、内存、磁盘）
- 数据库性能（查询时间、连接数）

### 工具

- **Prometheus**：指标收集
- **Grafana**：可视化
- **ELK**：日志分析

---

## 八、最佳实践

### 1. 测试先行（TDD）

- 先写测试，再写代码
- 保证代码质量

### 2. 快速反馈

- CI 流程应该在 10 分钟内完成
- 及时发现问题

### 3. 自动化一切

- 构建、测试、部署全部自动化
- 减少人工错误

### 4. 版本控制

- 所有配置都纳入版本控制
- 可追溯、可回滚

---

## 九、实施步骤

### 第一阶段：基础测试（1-2周）

1. 配置测试框架
2. 编写关键功能的单元测试
3. 配置 CI 流程（构建 + 测试）

### 第二阶段：集成测试（1-2周）

1. 编写集成测试
2. 配置测试数据库
3. 完善 CI 流程

### 第三阶段：自动化部署（1周）

1. 配置 Docker
2. 配置部署脚本
3. 实现 CD 流程

### 第四阶段：完善和优化（持续）

1. 提高测试覆盖率
2. 优化 CI/CD 流程
3. 添加监控和告警

---

## 十、总结

### CI/CD 的价值

1. **提高效率**：自动化减少人工操作
2. **保证质量**：自动测试及早发现问题
3. **快速交付**：缩短发布周期
4. **降低风险**：自动化减少人为错误

### 测试的价值

1. **保证质量**：确保功能正确
2. **文档作用**：测试即文档
3. **重构信心**：有测试才能放心重构
4. **回归测试**：防止新功能破坏旧功能

---

**下一步**：我将为您创建具体的配置文件和示例代码。

