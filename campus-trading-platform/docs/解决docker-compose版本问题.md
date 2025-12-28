# 解决 docker-compose 版本问题

## 问题现象

虽然 Docker 已经升级到 29.1.3，但执行 `docker-compose pull` 时仍然报错：
```
Error response from daemon: client version 1.43 is too old. 
Minimum supported API version is 1.44
```

## 原因分析

`docker-compose`（V1）可能使用的是旧的客户端。应该使用 `docker compose`（V2，作为 Docker 插件）。

## 解决方案

### 方法 1：使用 docker compose（V2，推荐）

```bash
cd /opt/campus-trading-platform
export DOCKER_USERNAME=yxw555

# 使用 docker compose（V2，注意没有横线）
sudo docker compose pull
sudo docker compose up -d
sudo docker compose ps
```

### 方法 2：重启 Docker 服务

如果方法 1 不行，尝试重启 Docker 服务：

```bash
# 重启 Docker 服务
sudo systemctl restart docker

# 然后重试
cd /opt/campus-trading-platform
export DOCKER_USERNAME=yxw555
sudo docker-compose pull
```

### 方法 3：检查并修复环境变量问题

错误信息显示镜像名称是 `yourusername`，说明环境变量没有生效。

**检查 docker-compose.yml 文件**：

```bash
# 查看文件内容
cat /opt/campus-trading-platform/docker-compose.yml | grep image
```

**如果显示 `yourusername`，需要修复**：

```bash
# 方法 3.1：直接在命令中设置环境变量
cd /opt/campus-trading-platform
DOCKER_USERNAME=yxw555 sudo docker compose pull

# 方法 3.2：或者修改 docker-compose.yml 文件
# 编辑文件，将 yourusername 替换为 yxw555
sudo nano /opt/campus-trading-platform/docker-compose.yml
# 找到 yourusername，替换为 yxw555
```

## 推荐操作流程

```bash
# 1. 进入目录
cd /opt/campus-trading-platform

# 2. 检查 docker-compose.yml 文件中的镜像名称
grep -i "image:" docker-compose.yml

# 3. 如果显示 yourusername，需要修复
# 方法 A：使用环境变量（推荐）
export DOCKER_USERNAME=yxw555
sudo docker compose pull

# 方法 B：或者直接修改文件
sudo sed -i 's/yourusername/yxw555/g' docker-compose.yml
sudo docker compose pull

# 4. 启动服务
sudo docker compose up -d

# 5. 查看状态
sudo docker compose ps

# 6. 查看日志
sudo docker compose logs -f
```

## 验证

```bash
# 检查镜像名称是否正确
sudo docker compose config | grep image

# 应该显示：
# image: yxw555/campus-trading-backend:latest
# image: yxw555/campus-trading-frontend:latest
```

## 常见问题

### Q: docker-compose 和 docker compose 有什么区别？

**A**: 
- `docker-compose`：V1，独立工具，可能使用旧客户端
- `docker compose`：V2，Docker 插件，使用新的客户端

推荐使用 `docker compose`（V2）。

### Q: 如何检查使用的是哪个版本？

**A**:
```bash
# 检查 V1
docker-compose --version

# 检查 V2
docker compose version
```

### Q: 环境变量不生效怎么办？

**A**: 
1. 确保在同一个 shell 会话中设置和使用
2. 或者直接在命令前设置：`DOCKER_USERNAME=yxw555 sudo docker compose pull`
3. 或者修改 docker-compose.yml 文件，直接替换占位符

