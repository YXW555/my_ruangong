#!/bin/bash
# 服务器初始化脚本 - 安装 Docker 和 Docker Compose

echo "========== 开始初始化服务器 =========="

# 更新系统包
echo "更新系统包..."
sudo apt update
sudo apt upgrade -y

# 安装必要的工具
echo "安装必要工具..."
sudo apt install -y curl wget git vim

# 安装 Docker
echo "安装 Docker..."
# 卸载旧版本（如果有）
sudo apt-get remove -y docker docker-engine docker.io containerd runc

# 安装依赖
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# 添加 Docker 官方 GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 设置仓库
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 安装 Docker Engine
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 将当前用户添加到 docker 组（避免每次都用 sudo）
sudo usermod -aG docker $USER

# 安装 Docker Compose（独立版本，如果需要）
echo "安装 Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/download/v2.23.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 创建项目目录
echo "创建项目目录..."
sudo mkdir -p /opt/campus-trading-platform
sudo chown -R $USER:$USER /opt/campus-trading-platform

# 验证安装
echo "========== 验证安装 =========="
docker --version
docker-compose --version
docker ps

echo "========== 初始化完成 =========="
echo "请重新登录服务器使 docker 组权限生效，或执行：newgrp docker"

