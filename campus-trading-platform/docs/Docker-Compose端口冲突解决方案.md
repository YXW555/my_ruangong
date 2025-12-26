# Docker Compose 端口冲突解决方案

## 问题描述

启动 Docker Compose 时遇到错误：
```
Error: ports are not available: exposing port TCP 0.0.0.0:3306 -> 127.0.0.1:0: 
listen tcp 0.0.0.0:3306: bind: Only one usage of each socket address (protocol/network address/port) is normally permitted.
```

## 错误原因

**端口 3306 已被占用**
- 本地已安装 MySQL 服务，占用了 3306 端口
- Docker Compose 无法绑定到已被占用的端口

---

## 解决方案

### 方案一：停止本地 MySQL 服务（推荐）

如果您不需要本地 MySQL，可以停止它：

#### Windows：

**方法 1：使用服务管理器**
1. 按 `Win + R`，输入 `services.msc`
2. 找到 `MySQL` 或 `MySQL80` 服务
3. 右键 → 停止

**方法 2：使用命令行**
```powershell
# 停止 MySQL 服务
net stop MySQL80
# 或
net stop MySQL
```

**方法 3：使用任务管理器**
1. 按 `Ctrl + Shift + Esc` 打开任务管理器
2. 找到 `mysqld.exe` 进程（PID 7120）
3. 右键 → 结束任务

#### Linux/macOS：
```bash
# 停止 MySQL 服务
sudo systemctl stop mysql
# 或
sudo service mysql stop
```

---

### 方案二：修改 Docker Compose 端口映射

如果不想停止本地 MySQL，可以修改 `docker-compose.yml`，使用不同的端口：

#### 修改 MySQL 端口映射

将 `3306:3306` 改为 `3307:3306`（或其他可用端口）：

```yaml
mysql:
  ports:
    - "3307:3306"  # 主机端口:容器端口
```

**注意**：如果修改了端口，需要更新后端连接配置：
- 如果后端需要从主机连接 MySQL，使用 `localhost:3307`
- 如果后端在 Docker 容器内，仍然使用 `mysql:3306`（容器内通信）

---

### 方案三：使用本地 MySQL（不启动 Docker MySQL）

如果您想使用本地 MySQL，可以：

1. **注释掉 Docker Compose 中的 MySQL 服务**
2. **修改后端配置，连接到本地 MySQL**

#### 修改 docker-compose.yml：

```yaml
services:
  # MySQL 数据库（注释掉，使用本地 MySQL）
  # mysql:
  #   image: mysql:8.0
  #   ...

  backend:
    environment:
      # 连接到本地 MySQL（使用 host.docker.internal）
      - SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/second_hand_trading?useSSL=false&serverTimezone=UTC
```

**Windows/Mac**：使用 `host.docker.internal`  
**Linux**：使用 `172.17.0.1` 或主机 IP

---

## 检查端口占用

### Windows：
```powershell
# 查看 3306 端口占用
netstat -ano | findstr :3306

# 查看进程详情
tasklist | findstr 7120
```

### Linux/macOS：
```bash
# 查看 3306 端口占用
lsof -i :3306
# 或
netstat -tulpn | grep 3306
```

---

## 推荐方案

### 开发环境：方案一（停止本地 MySQL）
- ✅ 简单直接
- ✅ 使用 Docker 统一管理
- ✅ 环境一致性好

### 生产环境：方案三（使用本地 MySQL）
- ✅ 性能更好
- ✅ 数据持久化更可靠
- ✅ 便于备份和管理

---

## 其他端口冲突

如果其他端口也有冲突，可以类似处理：

| 服务 | 默认端口 | 可改为 |
|------|---------|--------|
| MySQL | 3306 | 3307, 3308 |
| Redis | 6379 | 6380, 6381 |
| 后端 | 8080 | 8081, 8082 |
| 前端 | 80 | 8080, 8081 |

---

## 验证修复

修复后，重新启动：

```bash
# 停止现有容器
docker-compose down

# 重新启动
docker-compose up -d

# 查看状态
docker-compose ps
```

---

## 常见问题

### Q1: 如何永久停止本地 MySQL？

**A**: 
- Windows：在服务管理器中，将 MySQL 服务设置为"禁用"
- Linux：`sudo systemctl disable mysql`

### Q2: 修改端口后如何连接？

**A**: 
- 从主机连接：`localhost:新端口`
- 从容器连接：`服务名:容器端口`（不变）

### Q3: 如何查看所有端口占用？

**A**: 
- Windows：`netstat -ano`
- Linux：`netstat -tulpn` 或 `ss -tulpn`

