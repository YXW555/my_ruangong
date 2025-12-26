# CI/CD 和自动化测试快速开始指南

## 一、概述

本指南将帮助您快速搭建 CI/CD 流水线和自动化测试环境。

### 已创建的文件

1. **CI/CD 配置**
   - `.github/workflows/ci-cd.yml` - GitHub Actions 工作流
   - `docker-compose.yml` - Docker Compose 配置
   - `backend/Dockerfile` - 后端 Docker 镜像
   - `frontend/Dockerfile` - 前端 Docker 镜像

2. **测试文件**
   - `backend/src/test/java/.../UserServiceTest.java` - Service 层测试示例
   - `backend/src/test/java/.../UserControllerTest.java` - Controller 层测试示例
   - `backend/src/test/resources/application-test.properties` - 测试环境配置

---

## 二、快速开始（5 步）

### 步骤 1：运行本地测试（2 分钟）

```bash
# 进入后端目录
cd backend

# 运行所有测试
mvn test

# 查看测试报告
# 报告位置：backend/target/surefire-reports/index.html
```

### 步骤 2：查看测试覆盖率（1 分钟）

```bash
# 生成覆盖率报告
mvn jacoco:report

# 查看覆盖率报告
# 报告位置：backend/target/site/jacoco/index.html
```

### 步骤 3：本地构建 Docker 镜像（5 分钟）

```bash
# 构建后端镜像
cd backend
docker build -t campus-trading-backend:latest .

# 构建前端镜像
cd ../frontend
docker build -t campus-trading-frontend:latest .
```

### 步骤 4：使用 Docker Compose 启动（2 分钟）

```bash
# 在项目根目录
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 步骤 5：配置 GitHub Actions（10 分钟）

1. **推送代码到 GitHub**
   ```bash
   git add .
   git commit -m "添加 CI/CD 配置"
   git push origin main
   ```

2. **配置 GitHub Secrets**（如果需要部署）
   - 进入 GitHub 仓库 → Settings → Secrets → Actions
   - 添加以下 Secrets：
     - `DOCKER_USERNAME` - Docker Hub 用户名
     - `DOCKER_PASSWORD` - Docker Hub 密码
     - `HOST` - 服务器 IP（可选）
     - `USERNAME` - 服务器用户名（可选）
     - `SSH_KEY` - SSH 私钥（可选）

3. **查看 CI/CD 运行结果**
   - 进入 GitHub 仓库 → Actions 标签
   - 查看工作流运行状态

---

## 三、测试流程详解

### 1. 单元测试（Unit Test）

**位置**：`backend/src/test/java/com/second/hand/trading/server/service/`

**示例**：`UserServiceTest.java`

**运行**：
```bash
mvn test -Dtest=UserServiceTest
```

**特点**：
- ✅ 快速（毫秒级）
- ✅ 隔离（不依赖外部服务）
- ✅ 覆盖率高

### 2. 集成测试（Integration Test）

**位置**：`backend/src/test/java/com/second/hand/trading/server/controller/`

**示例**：`UserControllerTest.java`

**运行**：
```bash
mvn test -Dtest=UserControllerTest
```

**特点**：
- ✅ 测试完整流程
- ✅ 使用 MockMvc 模拟 HTTP 请求
- ✅ 验证 Controller 层逻辑

### 3. 端到端测试（E2E Test）

**工具**：Cypress（前端）

**安装**：
```bash
cd frontend
npm install --save-dev cypress
```

**运行**：
```bash
npx cypress open
```

---

## 四、CI/CD 流程说明

### GitHub Actions 工作流

```
代码推送
    ↓
后端构建（Maven）
    ↓
运行单元测试
    ↓
前端构建（npm）
    ↓
构建 Docker 镜像
    ↓
部署到服务器（可选）
```

### 触发条件

- **Push 到 main/master/develop 分支**
- **创建 Pull Request**

### 工作流步骤

1. **后端构建和测试**
   - 启动 MySQL 和 Redis 服务
   - 编译 Java 代码
   - 运行单元测试
   - 生成测试报告
   - 打包 JAR 文件

2. **前端构建**
   - 安装 npm 依赖
   - 运行 Lint 检查
   - 构建生产版本

3. **Docker 镜像构建**（仅 main 分支）
   - 构建后端镜像
   - 构建前端镜像
   - 推送到 Docker Hub

4. **部署**（仅 main 分支，可选）
   - SSH 连接到服务器
   - 拉取最新镜像
   - 重启服务

---

## 五、测试最佳实践

### 1. 测试命名规范

```java
// 好的命名
@Test
void testUserLogin_WithValidCredentials_ShouldReturnUser() { }

// 不好的命名
@Test
void test1() { }
```

### 2. 测试结构（AAA 模式）

```java
@Test
void testExample() {
    // Arrange（准备）
    String input = "test";
    
    // Act（执行）
    String result = service.doSomething(input);
    
    // Assert（断言）
    assertEquals("expected", result);
}
```

### 3. 测试覆盖率目标

- **单元测试**：70%+
- **集成测试**：50%+
- **关键业务逻辑**：100%

### 4. 测试数据管理

```java
// 使用 @Transactional 确保测试后回滚
@Transactional
class UserServiceTest {
    // 测试数据不会污染数据库
}
```

---

## 六、常见问题

### Q1: 测试失败怎么办？

**A**: 检查以下几点：
1. 数据库连接是否正常
2. Redis 是否启动
3. 测试数据是否正确
4. 测试环境配置是否正确

### Q2: Docker 构建失败？

**A**: 检查以下几点：
1. Dockerfile 路径是否正确
2. 依赖是否完整
3. 端口是否被占用

### Q3: GitHub Actions 不运行？

**A**: 检查以下几点：
1. 工作流文件路径是否正确（`.github/workflows/`）
2. 触发条件是否满足
3. 是否有语法错误

### Q4: 如何跳过测试？

**A**: 
```bash
# Maven
mvn package -DskipTests

# 不推荐，但有时需要
```

---

## 七、下一步

### 1. 编写更多测试

- 为每个 Service 编写单元测试
- 为每个 Controller 编写集成测试
- 提高测试覆盖率

### 2. 完善 CI/CD

- 添加代码质量检查（SonarQube）
- 添加安全扫描
- 添加性能测试

### 3. 监控和告警

- 集成 Prometheus
- 配置 Grafana 仪表板
- 设置告警规则

---

## 八、参考资源

- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 文档](https://junit.org/junit5/docs/current/user-guide/)
- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Docker 文档](https://docs.docker.com/)

---

**祝您测试顺利！** 🚀

