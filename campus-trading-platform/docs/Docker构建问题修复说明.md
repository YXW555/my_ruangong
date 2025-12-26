# Docker 构建问题修复说明

## 问题描述

构建 Docker 镜像时遇到错误：
```
ERROR [internal] load metadata for docker.io/library/openjdk:8-jre-slim
```

## 错误原因

1. **镜像已废弃**：`openjdk:8-jre-slim` 镜像可能已经不再维护或从 Docker Hub 移除
2. **网络问题**：无法连接到 Docker Hub 获取镜像元数据
3. **镜像标签不存在**：指定的标签可能已经不存在

## 解决方案

### 方案一：使用 Eclipse Temurin（推荐）

Eclipse Temurin 是 AdoptOpenJDK 的官方继任者，提供长期支持的 OpenJDK 构建。

**修改后的 Dockerfile：**
```dockerfile
# 第一阶段：构建
FROM maven:3.8.6-eclipse-temurin-8 AS build

# 第二阶段：运行
FROM eclipse-temurin:8-jre-alpine
```

**优点**：
- ✅ 官方维护，长期支持
- ✅ 镜像更小（alpine 版本）
- ✅ 稳定可靠

### 方案二：使用 Amazon Corretto

如果 Eclipse Temurin 也有问题，可以使用 Amazon Corretto：

```dockerfile
# 第一阶段：构建
FROM maven:3.8.6-amazoncorretto-8 AS build

# 第二阶段：运行
FROM amazoncorretto:8-alpine-jre
```

### 方案三：使用国内镜像源

如果网络问题，可以配置 Docker 使用国内镜像源：

**Windows Docker Desktop：**
1. 打开 Docker Desktop
2. 进入 Settings → Docker Engine
3. 添加镜像源配置：
```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
```

**Linux：**
编辑 `/etc/docker/daemon.json`：
```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
```

然后重启 Docker：
```bash
sudo systemctl restart docker
```

## 验证修复

运行以下命令验证：

```bash
# 构建镜像
docker build -t campus-trading-backend:latest .

# 查看镜像
docker images | grep campus-trading-backend

# 运行容器测试
docker run -p 8080:8080 campus-trading-backend:latest
```

## 常见问题

### Q1: 仍然无法拉取镜像？

**A**: 尝试：
1. 检查网络连接
2. 使用 VPN 或代理
3. 手动拉取镜像：`docker pull eclipse-temurin:8-jre-alpine`
4. 使用国内镜像源

### Q2: 构建速度慢？

**A**: 
1. 使用多阶段构建（已实现）
2. 利用 Docker 缓存（已实现）
3. 使用国内镜像源加速

### Q3: 镜像太大？

**A**: 
1. 使用 `alpine` 版本（已使用）
2. 多阶段构建，只复制必要的文件（已实现）
3. 清理构建缓存：`docker builder prune`

## 推荐的镜像选择

| 镜像 | 大小 | 维护状态 | 推荐度 |
|------|------|----------|--------|
| `eclipse-temurin:8-jre-alpine` | ~150MB | ✅ 官方维护 | ⭐⭐⭐⭐⭐ |
| `amazoncorretto:8-alpine-jre` | ~150MB | ✅ 官方维护 | ⭐⭐⭐⭐ |
| `openjdk:8-jre-slim` | ~200MB | ❌ 已废弃 | ⭐ |

## 总结

已更新 Dockerfile 使用 `eclipse-temurin:8-jre-alpine`，这是目前最推荐的 Java 8 运行时镜像。

如果仍有问题，请检查：
1. Docker 版本是否最新
2. 网络连接是否正常
3. Docker Hub 是否可访问

