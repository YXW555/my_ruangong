# 修复 Docker GPG 密钥错误

## 错误信息

```
GPG error: https://download.docker.com/linux/ubuntu jammy InRelease: 
The following signatures couldn't be verified because the public key is not available: 
NO_PUBKEY 7EA0A9C3F273FCD8
```

## 快速修复（推荐）

### 方法 1：使用官方安装脚本（最简单）

这个脚本会自动处理所有 GPG 密钥问题：

```bash
# 直接运行官方安装脚本
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 验证
sudo docker --version
sudo docker compose version
```

### 方法 2：手动修复 GPG 密钥

如果只想修复密钥问题，不重新安装 Docker：

```bash
# 方法 2.1：添加缺失的 GPG 密钥
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 7EA0A9C3F273FCD8

# 然后更新
sudo apt-get update
```

如果上面的方法不行，使用备用方法：

```bash
# 方法 2.2：使用新的密钥存储方式
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 更新
sudo apt-get update
```

### 方法 3：完全重新配置（如果上面都不行）

```bash
# 1. 清理旧的配置
sudo rm -f /etc/apt/sources.list.d/docker*.list
sudo rm -f /etc/apt/trusted.gpg.d/docker*.gpg
sudo rm -f /usr/share/keyrings/docker*.gpg

# 2. 安装必要工具
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# 3. 创建密钥目录
sudo mkdir -p /etc/apt/keyrings

# 4. 添加 Docker GPG 密钥（新方法）
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 5. 设置权限
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# 6. 添加 Docker 仓库
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 7. 更新包列表
sudo apt-get update

# 8. 现在应该可以正常安装或升级 Docker 了
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

## 验证修复

```bash
# 检查是否还有 GPG 错误
sudo apt-get update

# 如果成功，应该没有 GPG 错误了
# 然后可以安装或升级 Docker
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 验证
sudo docker --version
sudo docker compose version
```

## 推荐操作流程

**最简单的方法**（推荐）：

```bash
# 一步到位，自动处理所有问题
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
rm -f get-docker.sh

# 验证
sudo docker --version
sudo docker compose version
```

这个脚本会：
- 自动检测系统
- 自动配置 GPG 密钥
- 自动配置仓库
- 自动安装最新版本的 Docker
- 自动启动服务

## 如果还是有问题

1. **检查网络连接**：
   ```bash
   curl -I https://download.docker.com
   ```

2. **检查防火墙**：
   ```bash
   sudo ufw status
   ```

3. **使用国内镜像源**（如果网络慢）：
   ```bash
   # 使用阿里云镜像（如果官方源太慢）
   # 但官方安装脚本会自动选择最快的源
   ```

4. **查看详细错误**：
   ```bash
   sudo apt-get update 2>&1 | grep -i error
   ```

