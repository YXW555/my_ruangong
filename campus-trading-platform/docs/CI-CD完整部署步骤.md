# CI/CD 完整部署步骤指南

## 📋 概述

本指南将帮助你完成从零到生产环境的完整 CI/CD 部署流程。你已经完成了基础配置，现在需要完成服务器配置和 GitHub Actions 设置。

---

## ✅ 已完成的工作

根据项目结构，你已经完成了：

1. ✅ **Docker 配置**
   - `backend/Dockerfile` - 后端多阶段构建
   - `frontend/Dockerfile` - 前端多阶段构建
   - `docker-compose.yml` - 本地开发环境
   - `deploy/docker-compose.prod.yml` - 生产环境配置

2. ✅ **部署脚本**
   - `deploy/server-init.sh` - 服务器初始化脚本
   - `deploy/README-部署指南.md` - 部署文档

3. ✅ **测试配置**
   - JUnit 5 测试框架
   - JaCoCo 代码覆盖率
   - 测试用例已编写

4. ✅ **CI/CD 工作流文件**
   - `.github/workflows/ci-cd.yml` - GitHub Actions 配置

---

## 🚀 接下来的步骤

### 第一步：配置阿里云服务器安全组

1. **登录阿里云控制台**
   - 访问：https://ecs.console.aliyun.com
   - 找到你的 ECS 实例

2. **配置安全组规则**
   - 进入 **网络与安全** → **安全组** → **配置规则**
   - 添加以下入站规则：

| 端口范围 | 协议 | 授权对象 | 描述 |
|---------|------|---------|------|
| 22/22 | TCP | 0.0.0.0/0 | SSH 连接 |
| 80/80 | TCP | 0.0.0.0/0 | HTTP（前端） |
| 8080/8080 | TCP | 0.0.0.0/0 | 后端 API |
| 3306/3306 | TCP | 内网IP | MySQL（建议仅内网） |
| 6379/6379 | TCP | 内网IP | Redis（建议仅内网） |

**⚠️ 注意**：3306 和 6379 端口建议只允许内网访问，如果只是测试可以暂时开放 `0.0.0.0/0`。

---

### 第二步：初始化服务器环境

#### 2.1 连接服务器

**Windows PowerShell 或 Git Bash：**

```bash
ssh root@你的服务器IP
# 输入服务器密码
```

**或者使用阿里云控制台的"远程连接"功能**

#### 2.2 上传并执行初始化脚本

**方法 A：使用 SCP 上传（推荐）**

在本地项目目录执行：

```bash
# 上传初始化脚本
scp campus-trading-platform/deploy/server-init.sh root@你的服务器IP:/tmp/

# 连接服务器
ssh root@你的服务器IP

# 在服务器上执行
chmod +x /tmp/server-init.sh
sudo /tmp/server-init.sh

# 重新登录使 docker 组权限生效
exit
ssh root@你的服务器IP
```

**方法 B：直接在服务器上创建**

```bash
# 连接服务器后
mkdir -p /opt/campus-trading-platform/deploy
cd /opt/campus-trading-platform/deploy

# 创建文件
nano server-init.sh
# 复制 deploy/server-init.sh 的内容，粘贴，保存（Ctrl+O, Enter, Ctrl+X）

# 执行
chmod +x server-init.sh
sudo ./server-init.sh

# 重新登录
exit
ssh root@你的服务器IP
```

#### 2.3 验证安装

```bash
docker --version
docker-compose --version
docker ps
```

如果 `docker ps` 需要 sudo，执行：

```bash
newgrp docker
# 或者重新登录
```

---

### 第三步：配置 SSH 密钥（用于 GitHub Actions 自动部署）

#### 3.1 在本地生成 SSH 密钥对

**Windows PowerShell：**

```powershell
# 生成密钥对
ssh-keygen -t rsa -b 4096 -C "github-actions-deploy"

# 按提示操作：
# - 保存位置：直接回车（默认：C:\Users\你的用户名\.ssh\id_rsa）
# - 密码：可以设置，也可以直接回车（不设置）
```

**Git Bash：**

```bash
ssh-keygen -t rsa -b 4096 -C "github-actions-deploy"
```

#### 3.2 查看公钥和私钥

**Windows PowerShell：**

```powershell
# 查看公钥（要添加到服务器）
Get-Content ~/.ssh/id_rsa.pub

# 查看私钥（要添加到 GitHub Secrets）
Get-Content ~/.ssh/id_rsa
```

**Git Bash：**

```bash
# 查看公钥
cat ~/.ssh/id_rsa.pub

# 查看私钥
cat ~/.ssh/id_rsa
```

#### 3.3 将公钥添加到服务器

**方法 A：使用 ssh-copy-id（如果支持）**

```bash
ssh-copy-id root@你的服务器IP
```

**方法 B：手动添加（推荐）**

```bash
# 1. 连接服务器
ssh root@你的服务器IP

# 2. 创建 .ssh 目录
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# 3. 添加公钥（复制刚才的公钥内容）
nano ~/.ssh/authorized_keys
# 粘贴公钥内容，保存（Ctrl+O, Enter, Ctrl+X）

# 4. 设置权限
chmod 600 ~/.ssh/authorized_keys
```

#### 3.4 测试 SSH 密钥连接

```bash
# 在本地电脑
ssh root@你的服务器IP
# 应该不需要输入密码就能连接
```

---

### 第四步：配置 GitHub Secrets

#### 4.1 进入 GitHub 仓库设置

1. 打开你的 GitHub 仓库
2. 点击 **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**

#### 4.2 添加以下 Secrets

| Name | Value | 说明 |
|------|-------|------|
| `DOCKER_USERNAME` | 你的Docker Hub用户名 | Docker Hub 用户名（如果已有） |
| `DOCKER_PASSWORD` | 你的Docker Hub密码 | Docker Hub 密码或访问令牌（如果已有） |
| `HOST` | 你的服务器IP | 例如：`8.152.208.79` |
| `USERNAME` | `root` | 服务器用户名 |
| `SSH_KEY` | 完整的私钥内容 | 从 `-----BEGIN OPENSSH PRIVATE KEY-----` 到 `-----END OPENSSH PRIVATE KEY-----` |

**添加 SSH_KEY 的详细步骤：**

1. 点击 **New repository secret**
2. Name: `SSH_KEY`
3. Value: 粘贴完整的私钥内容（包括 BEGIN 和 END 行）
4. 点击 **Add secret**

**⚠️ 重要提示：**
- 如果还没有 Docker Hub 账号，需要先注册：https://hub.docker.com
- Docker Hub 密码建议使用访问令牌（Access Token）而不是密码，更安全
  - 生成令牌：Docker Hub → Account Settings → Security → New Access Token

---

### 第五步：在服务器上准备部署文件

#### 5.1 创建 docker-compose.yml

```bash
# 在服务器上
cd /opt/campus-trading-platform
nano docker-compose.yml
```

**复制以下内容（记得替换 `你的Docker用户名`）：**

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: campus-trading-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-root123456}
      MYSQL_DATABASE: second_hand_trading
      MYSQL_USER: trading_user
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:-trading123456}
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    restart: always
    networks:
      - trading-network
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  redis:
    image: redis:6.2
    container_name: campus-trading-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    restart: always
    networks:
      - trading-network
    command: redis-server --appendonly yes

  backend:
    image: 你的Docker用户名/campus-trading-backend:latest
    container_name: campus-trading-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/second_hand_trading?serverTimezone=UTC&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=trading_user
      - SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD:-trading123456}
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PORT=6379
      - USER_FILE_PATH=/opt/upload
    depends_on:
      - mysql
      - redis
    restart: always
    networks:
      - trading-network
    volumes:
      - upload_data:/opt/upload

  frontend:
    image: 你的Docker用户名/campus-trading-frontend:latest
    container_name: campus-trading-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    restart: always
    networks:
      - trading-network

volumes:
  mysql_data:
  redis_data:
  upload_data:

networks:
  trading-network:
    driver: bridge
```

**重要**：将 `你的Docker用户名` 替换为你的实际 Docker Hub 用户名！

#### 5.2 准备数据库初始化脚本（可选）

如果需要导入数据库结构：

```bash
# 在服务器上
cd /opt/campus-trading-platform

# 上传 SQL 文件（在本地执行）
scp campus-trading-platform/backend/second_hand_trading.sql root@你的服务器IP:/opt/campus-trading-platform/init.sql

# 或者在服务器上直接创建
nano init.sql
# 粘贴你的数据库 SQL 脚本内容
```

---

### 第六步：测试手动部署（第一次）

#### 6.1 手动构建和推送镜像（测试）

在本地或 CI/CD 运行之前，可以先手动测试：

```bash
# 在本地项目目录

# 登录 Docker Hub
docker login

# 构建后端镜像
cd campus-trading-platform/backend
docker build -t 你的Docker用户名/campus-trading-backend:latest .

# 构建前端镜像
cd ../frontend
docker build -t 你的Docker用户名/campus-trading-frontend:latest .

# 推送镜像
docker push 你的Docker用户名/campus-trading-backend:latest
docker push 你的Docker用户名/campus-trading-frontend:latest
```

#### 6.2 在服务器上启动服务

```bash
# 在服务器上
cd /opt/campus-trading-platform

# 拉取镜像
docker-compose pull

# 启动服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

#### 6.3 测试访问

- 前端：`http://你的服务器IP`
- 后端 API：`http://你的服务器IP:8080`

---

### 第七步：触发自动部署

#### 7.1 提交代码到 GitHub

```bash
# 在本地项目目录
git add .
git commit -m "配置 CI/CD 自动部署"
git push origin main
```

#### 7.2 查看 GitHub Actions

1. 进入 GitHub 仓库
2. 点击 **Actions** 标签
3. 应该能看到工作流自动运行
4. 等待构建和部署完成

#### 7.3 验证部署

部署成功后，访问：
- 前端：`http://你的服务器IP`
- 后端：`http://你的服务器IP:8080`

---

## 🔧 常见问题排查

### Q1: SSH 连接失败

**解决**：
- 检查安全组是否开放了 22 端口
- 检查服务器密码是否正确
- 尝试使用阿里云控制台的"远程连接"
- 检查 SSH 密钥是否正确配置

### Q2: Docker 命令需要 sudo

**解决**：
- 执行 `newgrp docker` 或重新登录
- 确认用户已添加到 docker 组：`groups`
- 检查 `/etc/group` 文件中的 docker 组

### Q3: GitHub Actions 构建失败

**解决**：
- 检查 GitHub Secrets 是否正确配置
- 检查 Docker Hub 用户名和密码是否正确
- 查看 Actions 日志中的具体错误
- 检查 Dockerfile 路径是否正确

### Q4: GitHub Actions 部署失败

**解决**：
- 检查 GitHub Secrets 中的 `HOST`、`USERNAME`、`SSH_KEY` 是否正确
- 测试 SSH 密钥连接：`ssh root@你的服务器IP`
- 检查服务器上的 docker-compose.yml 文件是否存在
- 查看 Actions 日志中的具体错误信息

### Q5: 服务无法访问

**解决**：
- 检查安全组端口是否开放（80, 8080）
- 检查服务是否正常运行：`docker-compose ps`
- 查看服务日志：`docker-compose logs backend` 或 `docker-compose logs frontend`
- 检查防火墙设置：`sudo ufw status`

### Q6: 数据库连接失败

**解决**：
- 检查 MySQL 容器是否正常运行：`docker-compose ps mysql`
- 查看 MySQL 日志：`docker-compose logs mysql`
- 检查环境变量配置是否正确
- 确认数据库已初始化

### Q7: 镜像拉取失败

**解决**：
- 检查 Docker Hub 镜像是否存在
- 检查 Docker Hub 用户名是否正确
- 检查网络连接
- 尝试手动拉取：`docker pull 你的Docker用户名/campus-trading-backend:latest`

---

## 📊 CI/CD 流程说明

### 工作流触发条件

- **推送代码到 main/master/develop 分支** → 触发测试和构建
- **推送代码到 main/master 分支** → 触发构建、推送镜像和自动部署
- **Pull Request 到 main/master 分支** → 仅触发测试（不部署）

### 工作流步骤

1. **Backend Build & Test**
   - 编译 Java 代码
   - 运行单元测试
   - 生成测试覆盖率报告

2. **Frontend Build**
   - 安装依赖
   - 构建生产版本

3. **Docker Build & Push**（仅 main/master 分支）
   - 构建后端镜像
   - 构建前端镜像
   - 推送到 Docker Hub

4. **Deploy to Server**（仅 main/master 分支）
   - 通过 SSH 连接服务器
   - 拉取最新镜像
   - 重启服务

---

## 🎯 后续优化建议

1. **配置域名**
   - 将 IP 绑定到域名（如：`trading.example.com`）
   - 更新 docker-compose.yml 中的域名配置

2. **配置 HTTPS**
   - 使用 Let's Encrypt 免费证书
   - 配置 Nginx 反向代理
   - 更新前端 API 地址

3. **配置监控**
   - 添加服务监控（如 Prometheus + Grafana）
   - 配置告警通知
   - 监控服务器资源使用情况

4. **数据备份**
   - 定期备份数据库
   - 配置自动备份脚本
   - 备份上传到云存储

5. **环境分离**
   - 配置开发、测试、生产环境
   - 使用不同的 GitHub 分支触发不同环境部署

6. **回滚机制**
   - 保留历史镜像版本
   - 配置快速回滚脚本

---

## 📝 检查清单

完成以下所有项目后，你的 CI/CD 就完全配置好了：

- [ ] 阿里云安全组已配置（22, 80, 8080 端口）
- [ ] 服务器已安装 Docker 和 Docker Compose
- [ ] SSH 密钥已配置并测试通过
- [ ] GitHub Secrets 已配置（DOCKER_USERNAME, DOCKER_PASSWORD, HOST, USERNAME, SSH_KEY）
- [ ] 服务器上已创建 docker-compose.yml
- [ ] 数据库初始化脚本已准备（如需要）
- [ ] 手动部署测试成功
- [ ] GitHub Actions 工作流运行成功
- [ ] 自动部署验证通过

---

**完成以上步骤后，你的项目就可以实现自动测试和部署了！** 🚀

每次推送代码到 main 分支，GitHub Actions 会自动：
1. 运行测试
2. 构建 Docker 镜像
3. 推送到 Docker Hub
4. 部署到你的阿里云服务器

祝你部署顺利！

