# Docker 升级 - 网络问题解决方案

## 问题分析

你遇到的两个问题：
1. **GPG 密钥错误**：`NO_PUBKEY 7EA0A9C3F273FCD8`
2. **SSL 连接失败**：`curl: (35) OpenSSL SSL_connect: Connection reset by peer`

这说明无法连接到 Docker 官方源，可能是网络问题。

## 解决方案

### 方案 1：使用系统自带的 Docker（最简单，推荐）

Ubuntu 22.04 自带 Docker，直接安装即可：

```bash
# 1. 更新包列表
sudo apt-get update

# 2. 安装 Docker（使用 Ubuntu 官方源，不需要连接 Docker 官方源）
sudo apt-get install -y docker.io docker-compose

# 3. 启动服务
sudo systemctl start docker
sudo systemctl enable docker

# 4. 验证
sudo docker --version
sudo docker-compose --version

# 5. 检查版本（应该 >= 20.10）
sudo docker --version
```

**注意**：这个版本的 Docker 可能不是最新的，但应该满足 API 1.44 的要求。

### 方案 2：使用阿里云镜像源（如果方案1的版本太旧）

```bash
# 1. 清理旧的 Docker 配置
sudo rm -f /etc/apt/sources.list.d/docker*.list
sudo rm -f /etc/apt/trusted.gpg.d/docker*.gpg

# 2. 安装必要工具
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# 3. 使用阿里云镜像添加 GPG 密钥
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# 4. 添加阿里云 Docker 仓库
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 5. 更新并安装
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 6. 启动服务
sudo systemctl start docker
sudo systemctl enable docker

# 7. 验证
sudo docker --version
sudo docker compose version
```

### 方案 3：手动下载并安装（如果网络完全不通）

```bash
# 1. 先尝试使用系统自带的 Docker
sudo apt-get update
sudo apt-get install -y docker.io docker-compose

# 2. 检查版本
sudo docker --version

# 3. 如果版本太旧（< 20.10），可以尝试从其他源下载 deb 包
# 但通常系统自带的版本应该足够了
```

### 方案 4：使用国内安装脚本

```bash
# 使用国内镜像的安装脚本
curl -fsSL https://get.docker.com | sudo sh -s -- --mirror Aliyun

# 或者使用其他镜像
curl -fsSL https://get.docker.com | sudo sh -s -- --mirror AzureChinaCloud
```

## 推荐操作流程

**最简单的方法**（先试试这个）：

```bash
# 1. 直接安装系统自带的 Docker
sudo apt-get update
sudo apt-get install -y docker.io docker-compose

# 2. 启动服务
sudo systemctl start docker
sudo systemctl enable docker

# 3. 检查版本
sudo docker --version

# 4. 如果版本 >= 20.10，就可以用了
# 如果版本太旧，再尝试方案 2（使用阿里云镜像）
```

## 验证安装

```bash
# 检查 Docker 版本
sudo docker --version

# 检查 Docker Compose 版本
sudo docker-compose --version

# 测试 Docker 是否正常工作
sudo docker ps

# 如果一切正常，应该可以正常使用 docker-compose 了
cd /opt/campus-trading-platform
sudo docker-compose ps
```

## 如果系统自带的 Docker 版本太旧

如果 `docker.io` 的版本太旧（< 20.10），可以尝试：

```bash
# 使用阿里云镜像源（方案 2）
# 或者手动下载 deb 包安装

# 检查当前版本
sudo docker --version

# 如果显示类似 "Docker version 20.10.x" 或更高，就可以用了
# 如果显示 "Docker version 19.03.x" 或更低，需要升级
```

## 常见问题

### Q: 系统自带的 Docker 版本够用吗？

**A**: Ubuntu 22.04 自带的 Docker 通常是 20.10+ 版本，应该满足 API 1.44 的要求。先试试，如果不行再考虑其他方案。

### Q: 安装后还是提示版本太旧？

**A**: 可能需要重启 Docker 服务：
```bash
sudo systemctl restart docker
sudo docker --version
```

### Q: 如何检查 Docker API 版本？

**A**: 
```bash
sudo docker version
# 查看 Client API version，应该 >= 1.44
```

