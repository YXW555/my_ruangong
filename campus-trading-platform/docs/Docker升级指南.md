# Docker 升级指南（解决版本过旧问题）

## 问题确认

从你的输出可以看到：
- Docker 客户端版本是 1.43
- 需要的版本至少是 1.44
- 这导致 `docker-compose` 和 `docker` 命令都无法正常工作

## 解决方案：手动升级 Docker

在服务器上执行以下命令：

### 方法 1：使用官方安装脚本（推荐，最简单）

这个方法会自动处理所有 GPG 密钥和仓库配置问题：

```bash
# 1. 停止当前 Docker 服务（如果正在运行）
sudo systemctl stop docker
sudo systemctl stop docker.socket

# 2. 使用官方脚本升级 Docker（会自动处理 GPG 密钥）
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 3. 安装 Docker Compose V2（插件形式）
sudo apt-get update
sudo apt-get install -y docker-compose-plugin

# 4. 启动 Docker 服务
sudo systemctl start docker
sudo systemctl enable docker

# 5. 验证安装
sudo docker --version
sudo docker compose version

# 6. 清理
rm -f get-docker.sh
```

### 方法 1.5：如果遇到 GPG 密钥错误（修复密钥问题）

如果执行 `apt-get update` 时遇到 GPG 密钥错误，先修复密钥：

```bash
# 修复缺失的 GPG 密钥
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 7EA0A9C3F273FCD8

# 或者使用备用方法
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 然后更新
sudo apt-get update
```

### 方法 2：完全重新配置 Docker 仓库（如果方法1失败）

```bash
# 1. 移除旧的 Docker 仓库配置
sudo rm -f /etc/apt/sources.list.d/docker*.list
sudo rm -f /etc/apt/trusted.gpg.d/docker*.gpg
sudo rm -f /usr/share/keyrings/docker*.gpg

# 2. 安装必要的工具
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# 3. 添加 Docker 官方 GPG 密钥（使用新的方法）
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 4. 设置正确的权限
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# 5. 添加 Docker 仓库
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 6. 更新包列表
sudo apt-get update

# 7. 安装 Docker
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 8. 启动服务
sudo systemctl start docker
sudo systemctl enable docker

# 9. 验证
sudo docker --version
sudo docker compose version
```

### 方法 2：使用 apt 升级（Ubuntu）

```bash
# 1. 更新包列表
sudo apt-get update

# 2. 卸载旧版本 Docker（可选）
# sudo apt-get remove docker docker-engine docker.io containerd runc

# 3. 安装新版本 Docker
sudo apt-get install -y docker.io docker-compose-plugin

# 4. 启动服务
sudo systemctl start docker
sudo systemctl enable docker

# 5. 验证
sudo docker --version
sudo docker compose version
```

## 升级后验证

```bash
# 检查 Docker 版本（应该 >= 20.10）
sudo docker --version

# 检查 Docker Compose 版本
sudo docker compose version

# 测试 Docker 是否正常工作
sudo docker ps

# 如果一切正常，应该能正常使用
cd /opt/campus-trading-platform
sudo docker-compose ps
# 或者
sudo docker compose ps
```

## 升级后重新部署

```bash
cd /opt/campus-trading-platform

# 设置环境变量
export DOCKER_USERNAME=你的Docker用户名

# 重新拉取镜像
sudo docker-compose pull
# 或者
sudo docker compose pull

# 启动服务
sudo docker-compose up -d
# 或者
sudo docker compose up -d

# 查看状态
sudo docker-compose ps
# 或者
sudo docker compose ps

# 查看日志
sudo docker-compose logs -f
# 或者
sudo docker compose logs -f
```

## 常见问题

### Q: 升级后仍然提示版本太旧？

**A**: 可能需要重启服务器或重新登录：
```bash
# 重新加载 systemd
sudo systemctl daemon-reload
sudo systemctl restart docker

# 或者重启服务器
sudo reboot
```

### Q: 升级后无法使用 docker 命令？

**A**: 可能需要将用户添加到 docker 组：
```bash
# 添加当前用户到 docker 组
sudo usermod -aG docker $USER

# 重新登录或使用 newgrp
newgrp docker

# 测试（不需要 sudo）
docker ps
```

### Q: docker-compose 命令找不到？

**A**: 可能需要安装 Docker Compose：
```bash
# 安装 Docker Compose V2（推荐）
sudo apt-get install -y docker-compose-plugin

# 然后使用
docker compose version

# 或者安装旧版本（不推荐）
sudo apt-get install -y docker-compose
```

## 为什么 CI/CD 中的升级步骤没有生效？

可能的原因：
1. **升级步骤失败了**：检查 GitHub Actions 日志中的 "Upgrade Docker on server" 步骤
2. **升级步骤执行了，但用户权限问题**：可能需要使用 `sudo`
3. **升级步骤执行了，但服务没有重启**：可能需要手动重启 Docker 服务

建议：
- 先手动在服务器上升级 Docker（使用上面的方法）
- 然后再通过 CI/CD 部署
- 或者检查 CI/CD 日志，看看升级步骤是否有错误

