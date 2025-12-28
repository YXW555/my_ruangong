# 服务器部署完整指南

## 一、服务器信息

- **公网 IP**: 8.152.208.79
- **操作系统**: Ubuntu 22.04 64位
- **配置**: 2核4G

## 二、第一步：配置安全组（重要！）

### 在阿里云控制台操作：

1. 进入 ECS 控制台 → 找到你的服务器
2. 点击 **网络与安全组** 标签
3. 点击 **安全组** → **配置规则**
4. 添加以下入站规则：

| 端口范围 | 协议 | 授权对象 | 描述 |
|---------|------|---------|------|
| 22/22 | TCP | 0.0.0.0/0 | SSH 连接 |
| 80/80 | TCP | 0.0.0.0/0 | HTTP（前端） |
| 8080/8080 | TCP | 0.0.0.0/0 | 后端 API |
| 3306/3306 | TCP | 0.0.0.0/0 | MySQL（可选，建议只允许内网） |
| 6379/6379 | TCP | 0.0.0.0/0 | Redis（可选，建议只允许内网） |

**注意**：3306 和 6379 建议只允许内网访问，如果只是测试可以暂时开放。

## 三、第二步：连接服务器并初始化

### 1. 使用 SSH 连接服务器

**Windows 用户（使用 PowerShell 或 Git Bash）：**

```bash
# 使用密码连接（首次连接）
ssh root@8.152.208.79
# 输入服务器密码
```

**或者使用阿里云控制台的"远程连接"功能**

### 2. 上传初始化脚本到服务器

**方法 A：直接在服务器上创建**

```bash
# 连接服务器后
mkdir -p /opt/campus-trading-platform/deploy
cd /opt/campus-trading-platform/deploy

# 创建初始化脚本
nano server-init.sh
# 复制 server-init.sh 的内容，粘贴，保存（Ctrl+O, Enter, Ctrl+X）
```

**方法 B：使用 SCP 上传（在本地电脑执行）**

```bash
# 在项目根目录
scp deploy/server-init.sh root@8.152.208.79:/opt/campus-trading-platform/
```

### 3. 执行初始化脚本

```bash
# 在服务器上
cd /opt/campus-trading-platform
chmod +x server-init.sh
sudo ./server-init.sh
```

### 4. 重新登录使权限生效

```bash
# 退出当前会话
exit

# 重新连接
ssh root@8.152.208.79

# 或者执行（不退出）
newgrp docker
```

### 5. 验证安装

```bash
docker --version
docker-compose --version
docker ps
```

## 四、第三步：配置 SSH 密钥（用于 GitHub Actions）

### 1. 在本地电脑生成 SSH 密钥对

```bash
# 在本地电脑执行（Windows PowerShell 或 Git Bash）
ssh-keygen -t rsa -b 4096 -C "github-actions-deploy"

# 按提示操作：
# - 保存位置：直接回车（使用默认：C:\Users\你的用户名\.ssh\id_rsa）
# - 密码：可以设置，也可以直接回车（不设置）
```

### 2. 查看公钥和私钥

```bash
# 查看公钥（要添加到服务器）
cat ~/.ssh/id_rsa.pub
# 或者 Windows
type C:\Users\你的用户名\.ssh\id_rsa.pub

# 查看私钥（要添加到 GitHub Secrets）
cat ~/.ssh/id_rsa
# 或者 Windows
type C:\Users\你的用户名\.ssh\id_rsa
```

### 3. 将公钥添加到服务器

```bash
# 方法 A：使用 ssh-copy-id（如果支持）
ssh-copy-id root@8.152.208.79

# 方法 B：手动添加（在服务器上执行）
# 1. 连接服务器
ssh root@8.152.208.79

# 2. 创建 .ssh 目录
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# 3. 添加公钥（复制刚才的公钥内容）
nano ~/.ssh/authorized_keys
# 粘贴公钥内容，保存

# 4. 设置权限
chmod 600 ~/.ssh/authorized_keys
```

### 4. 测试 SSH 密钥连接

```bash
# 在本地电脑
ssh root@8.152.208.79
# 应该不需要输入密码就能连接
```

## 五、第四步：配置 GitHub Secrets

### 1. 进入 GitHub 仓库

- 打开你的仓库：`https://github.com/lxy1500/ruangong`
- 点击 **Settings** → **Secrets and variables** → **Actions**

### 2. 添加以下 Secrets

你已经有了 `DOCKER_USERNAME` 和 `DOCKER_PASSWORD`，还需要添加：

| Name | Value | 说明 |
|------|-------|------|
| `HOST` | `8.152.208.79` | 服务器公网 IP |
| `USERNAME` | `root` | 服务器用户名 |
| `SSH_KEY` | （你的私钥内容） | 完整的私钥，包括 `-----BEGIN OPENSSH PRIVATE KEY-----` 和 `-----END OPENSSH PRIVATE KEY-----` |

**添加 SSH_KEY 的步骤：**
1. 点击 **New repository secret**
2. Name: `SSH_KEY`
3. Value: 粘贴完整的私钥内容（从 `-----BEGIN` 到 `-----END`）
4. 点击 **Add secret**

## 六、第五步：准备部署文件

### 1. 在服务器上创建 docker-compose.yml

```bash
# 在服务器上
cd /opt/campus-trading-platform
nano docker-compose.yml
```

**复制以下内容（记得替换 DOCKER_USERNAME）：**

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: campus-trading-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123456
      MYSQL_DATABASE: second_hand_trading
      MYSQL_USER: trading_user
      MYSQL_PASSWORD: trading123456
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    restart: always
    networks:
      - trading-network

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

  backend:
    image: 你的Docker用户名/campus-trading-backend:latest
    container_name: campus-trading-backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/second_hand_trading?serverTimezone=UTC&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=trading_user
      - SPRING_DATASOURCE_PASSWORD=trading123456
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PORT=6379
    depends_on:
      - mysql
      - redis
    restart: always
    networks:
      - trading-network

  frontend:
    image: yxw555/campus-trading-frontend:latest
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

networks:
  trading-network:
    driver: bridge
```

**重要**：将 `你的Docker用户名` 替换为你的实际 Docker Hub 用户名！

### 2. 初始化数据库（可选）

如果需要导入数据库结构：

```bash
# 在服务器上
cd /opt/campus-trading-platform

# 创建数据库初始化脚本
nano init.sql
# 粘贴你的数据库 SQL 脚本内容
```

## 七、第六步：测试部署

### 1. 手动测试部署（第一次）

```bash
# 在服务器上
cd /opt/campus-trading-platform

# 启动服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 2. 测试访问

- 前端：`http://8.152.208.79`
- 后端 API：`http://8.152.208.79:8080`

## 八、第七步：触发自动部署

### 1. 推送代码到 main 分支

```bash
# 在本地项目目录
git add .
git commit -m "配置 CI/CD 自动部署"
git push origin main
```

### 2. 查看 GitHub Actions

- 进入 GitHub 仓库 → **Actions** 标签
- 应该能看到工作流自动运行
- 等待构建和部署完成

### 3. 验证部署

部署成功后，访问：
- 前端：`http://8.152.208.79`
- 后端：`http://8.152.208.79:8080`

## 九、常见问题

### Q1: SSH 连接失败

**解决**：
- 检查安全组是否开放了 22 端口
- 检查服务器密码是否正确
- 尝试使用阿里云控制台的"远程连接"

### Q2: Docker 命令需要 sudo

**解决**：
- 执行 `newgrp docker` 或重新登录
- 确认用户已添加到 docker 组：`groups`

### Q3: GitHub Actions 部署失败

**解决**：
- 检查 GitHub Secrets 是否正确配置
- 检查 SSH 密钥是否正确
- 查看 Actions 日志中的具体错误

### Q4: 服务无法访问

**解决**：
- 检查安全组端口是否开放
- 检查服务是否正常运行：`docker-compose ps`
- 查看服务日志：`docker-compose logs`

## 十、后续优化

1. **配置域名**：将 IP 绑定到域名
2. **配置 HTTPS**：使用 Let's Encrypt 免费证书
3. **配置监控**：添加服务监控和告警
4. **数据备份**：定期备份数据库

---

**完成以上步骤后，你的项目就可以自动部署了！** 🚀

