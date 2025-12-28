#!/bin/bash
# 快速安装脚本 - 只安装必要的 Docker 和 Docker Compose

echo "========== 快速安装 Docker =========="

# 更新系统（跳过，加快速度）
# sudo apt update

# 安装 Docker（使用官方一键安装脚本，最快）
echo "安装 Docker..."
curl -fsSL https://get.docker.com | sudo sh

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 将当前用户添加到 docker 组
sudo usermod -aG docker $USER

# 安装 Docker Compose（独立版本，更快）
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

echo "========== 安装完成 =========="
echo "请执行：newgrp docker 或重新登录使权限生效"

