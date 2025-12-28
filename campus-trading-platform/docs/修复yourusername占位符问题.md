# 修复 yourusername 占位符问题

## 问题原因

`docker-compose.yml` 文件中使用了 `${DOCKER_USERNAME:-yourusername}`，这是环境变量的默认值语法。如果 `DOCKER_USERNAME` 环境变量没有设置，就会使用默认值 `yourusername`，导致拉取不到镜像。

## 立即修复（在服务器上执行）

```bash
cd /opt/campus-trading-platform

# 直接替换文件中的 yourusername 为 yxw555
sudo sed -i 's/yourusername/yxw555/g' docker-compose.yml

# 验证修改
grep "image:" docker-compose.yml
# 应该显示：
# image: yxw555/campus-trading-backend:latest
# image: yxw555/campus-trading-frontend:latest

# 然后启动服务
sudo docker compose up -d

# 查看状态
sudo docker compose ps
```

## 长期解决方案

我已经修改了 CI/CD 工作流，在复制文件到服务器之前会自动替换占位符。下次部署时会自动修复。

但为了确保以后不再出现这个问题，有两种方案：

### 方案 1：直接替换占位符（推荐）

修改 `docker-compose.prod.yml`，直接使用你的 Docker Hub 用户名：

```yaml
backend:
  image: yxw555/campus-trading-backend:latest
  # ...

frontend:
  image: yxw555/campus-trading-frontend:latest
  # ...
```

### 方案 2：确保环境变量正确传递

在服务器上创建 `.env` 文件：

```bash
cd /opt/campus-trading-platform
echo "DOCKER_USERNAME=yxw555" | sudo tee .env
```

然后使用：
```bash
sudo docker compose up -d
```

## 验证修复

```bash
# 检查配置文件
cat /opt/campus-trading-platform/docker-compose.yml | grep image

# 应该显示：
# image: yxw555/campus-trading-backend:latest
# image: yxw555/campus-trading-frontend:latest
# 而不是 yourusername
```

