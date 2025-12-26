# CI/CD 和自动化测试说明

## 📋 目录

- [概述](#概述)
- [快速开始](#快速开始)
- [测试流程](#测试流程)
- [CI/CD 流程](#cicd-流程)
- [部署说明](#部署说明)

---

## 概述

本项目已配置完整的 CI/CD 流水线和自动化测试框架。

### 技术栈

- **CI/CD**: GitHub Actions
- **容器化**: Docker + Docker Compose
- **测试框架**: JUnit 5 + Spring Boot Test
- **代码覆盖率**: JaCoCo

---

## 快速开始

### 1. 运行测试

```bash
cd backend
mvn test
```

### 2. 查看测试覆盖率

```bash
mvn jacoco:report
# 打开 backend/target/site/jacoco/index.html
```

### 3. 构建 Docker 镜像

```bash
# 后端
cd backend && docker build -t campus-trading-backend:latest .

# 前端
cd frontend && docker build -t campus-trading-frontend:latest .
```

### 4. 启动服务

```bash
docker-compose up -d
```

---

## 测试流程

### 测试金字塔

```
        E2E测试
      (少量，关键流程)
    ────────────────
     集成测试
   (中等，模块交互)
  ────────────────────
    单元测试
  (大量，单个方法)
────────────────────────
```

### 1. 单元测试

**位置**: `backend/src/test/java/.../service/`

**示例**: `UserServiceTest.java`

**运行**:
```bash
mvn test -Dtest=UserServiceTest
```

### 2. 集成测试

**位置**: `backend/src/test/java/.../controller/`

**示例**: `UserControllerTest.java`

**运行**:
```bash
mvn test -Dtest=UserControllerTest
```

### 3. 端到端测试

**工具**: Cypress（可选）

**安装**:
```bash
cd frontend
npm install --save-dev cypress
```

---

## CI/CD 流程

### GitHub Actions 工作流

当代码推送到 `main`、`master` 或 `develop` 分支时，自动触发：

1. ✅ **后端构建和测试**
   - 编译 Java 代码
   - 运行单元测试
   - 生成测试报告

2. ✅ **前端构建**
   - 安装依赖
   - 构建生产版本

3. ✅ **Docker 镜像构建**（仅 main 分支）
   - 构建并推送镜像到 Docker Hub

4. ✅ **自动部署**（可选，仅 main 分支）
   - 部署到生产服务器

### 查看 CI/CD 状态

1. 进入 GitHub 仓库
2. 点击 **Actions** 标签
3. 查看工作流运行状态

---

## 部署说明

### 使用 Docker Compose

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 服务端口

- **前端**: http://localhost:80
- **后端**: http://localhost:8080
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

---

## 配置文件说明

### CI/CD 配置

- `.github/workflows/ci-cd.yml` - GitHub Actions 工作流
- `docker-compose.yml` - Docker Compose 配置
- `backend/Dockerfile` - 后端 Docker 镜像
- `frontend/Dockerfile` - 前端 Docker 镜像

### 测试配置

- `backend/src/test/resources/application-test.properties` - 测试环境配置
- `backend/pom.xml` - 测试依赖和插件配置

---

## 测试覆盖率

### 目标

- **单元测试**: 70%+
- **集成测试**: 50%+
- **关键业务逻辑**: 100%

### 查看覆盖率报告

```bash
cd backend
mvn jacoco:report
# 打开 target/site/jacoco/index.html
```

---

## 常见问题

### Q: 测试失败怎么办？

**A**: 检查：
1. 数据库连接配置
2. Redis 是否启动
3. 测试数据是否正确

### Q: Docker 构建失败？

**A**: 检查：
1. Dockerfile 路径
2. 依赖是否完整
3. 端口是否被占用

### Q: GitHub Actions 不运行？

**A**: 检查：
1. 工作流文件路径（`.github/workflows/`）
2. 触发条件
3. 语法错误

---

## 参考文档

- [CI/CD 和自动化测试指南](./docs/CI-CD和自动化测试指南.md) - 详细指南
- [CI/CD 快速开始指南](./docs/CI-CD快速开始指南.md) - 快速上手

---

**Happy Testing!** 🚀

