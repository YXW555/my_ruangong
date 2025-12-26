# 前端 Docker 构建问题修复说明

## 问题描述

构建前端 Docker 镜像时遇到错误：
```
ERROR [internal] load metadata for docker.io/library/node:14-alpine
ERROR [internal] load metadata for docker.io/library/nginx:alpine
```

## 错误原因

1. **网络问题**：无法连接到 Docker Hub 获取镜像元数据
2. **镜像源问题**：npm 默认使用官方源，下载速度慢或失败
3. **依赖安装问题**：某些依赖包可能需要特殊处理

## 解决方案

### 已修复的内容

1. **配置 npm 淘宝镜像源**
   ```dockerfile
   RUN npm config set registry https://registry.npmmirror.com
   ```
   - 使用淘宝镜像源加速 npm 包下载
   - 避免网络超时问题

2. **添加 `--legacy-peer-deps` 参数**
   ```dockerfile
   RUN npm ci --legacy-peer-deps
   ```
   - 解决 npm 7+ 的 peer dependencies 问题
   - 兼容旧版本的依赖关系

3. **创建 `.dockerignore` 文件**
   - 排除不必要的文件，减小构建上下文
   - 加快构建速度

## 如果仍然有问题

### 方案 1：配置 Docker 镜像加速器（推荐）

**Windows Docker Desktop：**
1. 打开 Docker Desktop
2. 进入 Settings → Docker Engine
3. 添加镜像源配置：
```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com"
  ]
}
```
4. 点击 Apply & Restart

**Linux：**
编辑 `/etc/docker/daemon.json`：
```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com"
  ]
}
```

然后重启 Docker：
```bash
sudo systemctl restart docker
```

### 方案 2：手动拉取镜像

```bash
# 拉取 Node.js 镜像
docker pull node:14-alpine

# 拉取 Nginx 镜像
docker pull nginx:alpine

# 然后再构建
docker build -t campus-trading-frontend:latest .
```

### 方案 3：使用更新的镜像版本

如果 `node:14-alpine` 有问题，可以尝试：

```dockerfile
# 使用 Node.js 16（更稳定）
FROM node:16-alpine AS build

# 或使用 Node.js 18 LTS
FROM node:18-alpine AS build
```

### 方案 4：使用国内 Docker 镜像仓库

如果 Docker Hub 无法访问，可以使用：

```dockerfile
# 使用阿里云镜像
FROM registry.cn-hangzhou.aliyuncs.com/acs/node:14-alpine AS build
FROM registry.cn-hangzhou.aliyuncs.com/acs/nginx:alpine
```

## 验证修复

运行以下命令验证：

```bash
# 构建镜像
cd frontend
docker build -t campus-trading-frontend:latest .

# 查看镜像
docker images | grep campus-trading-frontend

# 运行容器测试
docker run -p 80:80 campus-trading-frontend:latest
```

## 常见问题

### Q1: npm install 失败？

**A**: 尝试：
1. 检查网络连接
2. 使用 `npm ci --legacy-peer-deps`（已添加）
3. 清除 npm 缓存：`npm cache clean --force`
4. 使用 `npm install` 代替 `npm ci`（不推荐，但可以尝试）

### Q2: 构建速度慢？

**A**: 
1. 使用 npm 淘宝镜像源（已配置）
2. 使用 `.dockerignore` 排除不必要文件（已添加）
3. 多阶段构建，利用 Docker 缓存（已实现）

### Q3: 依赖版本冲突？

**A**: 
1. 使用 `--legacy-peer-deps`（已添加）
2. 更新 `package.json` 中的依赖版本
3. 使用 `npm install --force`（不推荐）

## 优化建议

### 1. 使用 .npmrc 文件（可选）

在 `frontend` 目录创建 `.npmrc` 文件：
```
registry=https://registry.npmmirror.com
```

### 2. 使用多阶段构建优化

当前 Dockerfile 已经使用了多阶段构建，这是最佳实践。

### 3. 添加健康检查

可以在 Dockerfile 中添加健康检查：
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1
```

## 总结

已修复前端 Dockerfile：
1. ✅ 配置 npm 淘宝镜像源
2. ✅ 添加 `--legacy-peer-deps` 参数
3. ✅ 创建 `.dockerignore` 文件

如果仍有问题，请：
1. 配置 Docker 镜像加速器
2. 手动拉取镜像
3. 检查网络连接

