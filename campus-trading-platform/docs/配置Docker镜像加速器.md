# 配置 Docker 镜像加速器（解决网络超时问题）

## 问题现象

执行 `docker login` 或 `docker pull` 时出现：
```
Error response from daemon: Get "https://registry-1.docker.io/v2/": 
context deadline exceeded (Client.Timeout exceeded while awaiting headers)
```

这是网络连接超时，无法访问 Docker Hub 官方源。

## 解决方案：配置国内镜像加速器

### 方法 1：配置 Docker 镜像加速器（推荐）

```bash
# 1. 创建或编辑 Docker daemon 配置文件
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://dockerproxy.com",
    "https://mirror.baidubce.com",
    "https://docker.nju.edu.cn"
  ]
}
EOF

# 2. 重启 Docker 服务
sudo systemctl daemon-reload
sudo systemctl restart docker

# 3. 验证配置
sudo docker info | grep -A 10 "Registry Mirrors"

# 4. 现在可以尝试拉取镜像了
cd /opt/campus-trading-platform
sudo docker compose pull
```

### 方法 2：如果镜像加速器还是慢，直接拉取（不登录）

如果镜像是公开的，可以不登录直接拉取：

```bash
cd /opt/campus-trading-platform

# 直接拉取镜像（使用镜像加速器）
sudo docker pull yxw555/campus-trading-backend:latest
sudo docker pull yxw555/campus-trading-frontend:latest

# 然后启动服务
sudo docker compose up -d
```

### 方法 3：使用阿里云镜像加速器（如果服务器是阿里云）

```bash
# 1. 登录阿里云控制台
# 2. 容器镜像服务 → 镜像加速器
# 3. 复制你的专属加速地址

# 4. 配置加速器
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://你的阿里云加速地址.mirror.aliyuncs.com"
  ]
}
EOF

# 5. 重启 Docker
sudo systemctl daemon-reload
sudo systemctl restart docker
```

## 推荐操作流程

```bash
# 1. 配置镜像加速器
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://dockerproxy.com",
    "https://mirror.baidubce.com"
  ]
}
EOF

# 2. 重启 Docker
sudo systemctl daemon-reload
sudo systemctl restart docker

# 3. 验证配置
sudo docker info | grep -A 5 "Registry Mirrors"

# 4. 直接拉取镜像（不需要登录，如果镜像是公开的）
cd /opt/campus-trading-platform
sudo docker pull yxw555/campus-trading-backend:latest
sudo docker pull yxw555/campus-trading-frontend:latest

# 5. 启动服务
sudo docker compose up -d

# 6. 查看状态
sudo docker compose ps
```

## 如果还是超时

### 方案 A：增加超时时间

```bash
# 编辑 Docker daemon 配置
sudo nano /etc/docker/daemon.json

# 添加超时配置
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://dockerproxy.com"
  ],
  "max-concurrent-downloads": 3,
  "max-concurrent-uploads": 5
}

# 重启 Docker
sudo systemctl restart docker
```

### 方案 B：使用代理（如果有）

```bash
# 配置 Docker 使用代理
sudo mkdir -p /etc/systemd/system/docker.service.d
sudo tee /etc/systemd/system/docker.service.d/http-proxy.conf <<-'EOF'
[Service]
Environment="HTTP_PROXY=http://代理地址:端口"
Environment="HTTPS_PROXY=http://代理地址:端口"
Environment="NO_PROXY=localhost,127.0.0.1"
EOF

# 重启 Docker
sudo systemctl daemon-reload
sudo systemctl restart docker
```

## 验证镜像加速器是否生效

```bash
# 测试拉取镜像速度
time sudo docker pull hello-world

# 如果很快（几秒钟），说明加速器生效了
```

## 常见镜像加速器地址

- **DaoCloud**: `https://docker.m.daocloud.io`
- **Docker Proxy**: `https://dockerproxy.com`
- **百度云**: `https://mirror.baidubce.com`
- **南京大学**: `https://docker.nju.edu.cn`
- **阿里云**: `https://你的ID.mirror.aliyuncs.com`（需要登录阿里云获取）

## 完整配置示例

```bash
# 一步到位配置
sudo mkdir -p /etc/docker && \
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.1ms.run",  
    "https://dockerproxy.com", 
    "https://hub.rat.dev"      
  ]
}
EOF
sudo systemctl daemon-reload && \
sudo systemctl restart docker && \
echo "镜像加速器配置完成！"
```

