# 从组长仓库迁移到个人仓库的 CI/CD 方案

## 📋 当前情况

- **组长仓库**：`D:/git/ruangong` → 组长的 GitHub 仓库（已尝试 CI/CD，未成功）
- **个人仓库**：`D:/git/my_ruangong` → 你的 GitHub 仓库（当前项目）
- **服务器**：同一个阿里云服务器

## ✅ 不需要重复做的操作

以下操作因为使用的是**同一个服务器**，所以**不需要重复**：

### 1. ✅ 服务器初始化（已完成）
- Docker 和 Docker Compose 已安装
- 服务器环境已配置
- **操作**：无需重复

### 2. ✅ SSH 密钥配置（如果使用相同密钥）
- 如果使用**同一个 SSH 密钥对**，服务器上的 `~/.ssh/authorized_keys` 已经配置好了
- **操作**：无需重复（除非你要用新的密钥）

### 3. ✅ 服务器安全组配置（已完成）
- 端口已开放（22, 80, 8080）
- **操作**：无需重复

### 4. ✅ 服务器上的目录结构（已存在）
- `/opt/campus-trading-platform` 目录已创建
- **操作**：无需重复

---

## 🔄 需要重新做的操作

以下操作因为使用的是**不同的 GitHub 仓库**，所以**必须重新配置**：

### 1. ⚠️ 配置你的 GitHub Secrets（必须）

在你的 GitHub 仓库中配置 Secrets：

1. 进入你的 GitHub 仓库
2. **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**

需要添加的 Secrets：

| Name | Value | 说明 |
|------|-------|------|
| `DOCKER_USERNAME` | 你的Docker Hub用户名 | 如果和组长不同，需要更新 |
| `DOCKER_PASSWORD` | 你的Docker Hub密码/令牌 | 如果和组长不同，需要更新 |
| `HOST` | 服务器IP | 应该和组长的一样（同一个服务器） |
| `USERNAME` | `root` | 服务器用户名（通常一样） |
| `SSH_KEY` | SSH私钥内容 | **如果使用同一个密钥对，直接复制组长的值** |

**重要提示**：
- 如果使用**同一个 SSH 密钥对**，`SSH_KEY` 可以直接复制组长仓库中的值
- 如果使用**不同的 Docker Hub 账号**，需要更新 `DOCKER_USERNAME` 和 `DOCKER_PASSWORD`

---

### 2. ⚠️ 检查并更新服务器上的 docker-compose.yml（可能需要）

如果 Docker Hub 用户名不同，需要更新服务器上的配置：

#### 2.1 检查当前配置

```bash
# 连接服务器
ssh root@你的服务器IP

# 查看当前的 docker-compose.yml
cat /opt/campus-trading-platform/docker-compose.yml
```

#### 2.2 如果 Docker Hub 用户名不同，需要更新

```bash
# 在服务器上编辑
cd /opt/campus-trading-platform
nano docker-compose.yml
```

**需要修改的地方**：
- `backend` 服务的 `image` 字段：`你的Docker用户名/campus-trading-backend:latest`
- `frontend` 服务的 `image` 字段：`你的Docker用户名/campus-trading-frontend:latest`

**或者**，使用环境变量（推荐）：

确保 `docker-compose.yml` 中使用环境变量：

```yaml
backend:
  image: ${DOCKER_USERNAME:-yourusername}/campus-trading-backend:latest
  # ...

frontend:
  image: ${DOCKER_USERNAME:-yourusername}/campus-trading-frontend:latest
  # ...
```

然后在服务器上设置环境变量：

```bash
# 在服务器上
cd /opt/campus-trading-platform
export DOCKER_USERNAME=你的Docker用户名
docker-compose up -d
```

**或者**，在 `~/.bashrc` 或 `~/.profile` 中添加：

```bash
echo 'export DOCKER_USERNAME=你的Docker用户名' >> ~/.bashrc
source ~/.bashrc
```

---

### 3. ⚠️ 确认项目已推送到你的 GitHub 仓库

```bash
# 在 D:/git/my_ruangong 目录下
cd campus-trading-platform

# 检查远程仓库
git remote -v

# 如果还没有设置远程仓库，添加你的 GitHub 仓库
git remote add origin https://github.com/你的用户名/你的仓库名.git

# 或者如果已经存在但指向组长的仓库，更新它
git remote set-url origin https://github.com/你的用户名/你的仓库名.git

# 推送代码
git add .
git commit -m "配置 CI/CD 到个人仓库"
git push -u origin main
# 或者 git push -u origin master（根据你的分支名）
```

---

### 4. ⚠️ 验证 GitHub Actions 工作流文件存在

确认你的项目中已经有 `.github/workflows/ci-cd.yml` 文件：

```bash
# 在 D:/git/my_ruangong/campus-trading-platform 目录下
ls -la .github/workflows/ci-cd.yml
```

如果文件不存在，需要从当前项目复制或创建。

---

## 📝 完整操作步骤（快速检查清单）

### 步骤 1：检查 SSH 密钥（5分钟）

**如果使用同一个 SSH 密钥对**：

```bash
# 在本地检查你的 SSH 私钥
# Windows PowerShell
Get-Content ~/.ssh/id_rsa

# 如果和组长用的是同一个密钥，直接复制这个内容到 GitHub Secrets 的 SSH_KEY
```

**如果要使用新的 SSH 密钥**：

```bash
# 生成新密钥
ssh-keygen -t rsa -b 4096 -C "my-github-actions-deploy"

# 查看公钥
Get-Content ~/.ssh/id_rsa.pub

# 添加到服务器
ssh-copy-id root@你的服务器IP
# 或者手动添加到服务器的 ~/.ssh/authorized_keys
```

---

### 步骤 2：配置 GitHub Secrets（10分钟）

1. 进入你的 GitHub 仓库
2. **Settings** → **Secrets and variables** → **Actions**
3. 添加以下 Secrets：

   - `DOCKER_USERNAME`：你的 Docker Hub 用户名
   - `DOCKER_PASSWORD`：你的 Docker Hub 密码或访问令牌
   - `HOST`：服务器 IP（和组长的一样）
   - `USERNAME`：`root`
   - `SSH_KEY`：SSH 私钥内容（如果使用同一个密钥，复制组长的值）

---

### 步骤 3：更新服务器配置（如果需要，5分钟）

**如果 Docker Hub 用户名不同**：

```bash
# 连接服务器
ssh root@你的服务器IP

# 方法 A：直接编辑 docker-compose.yml
cd /opt/campus-trading-platform
nano docker-compose.yml
# 修改 backend 和 frontend 的 image 字段为你的 Docker Hub 用户名

# 方法 B：使用环境变量（推荐）
export DOCKER_USERNAME=你的Docker用户名
# 然后确保 docker-compose.yml 使用 ${DOCKER_USERNAME}
```

**如果 Docker Hub 用户名相同**：

```bash
# 无需修改，直接跳过
```

---

### 步骤 4：推送代码并触发 CI/CD（5分钟）

```bash
# 在 D:/git/my_ruangong/campus-trading-platform 目录下

# 确认远程仓库指向你的 GitHub
git remote -v

# 如果不对，更新它
git remote set-url origin https://github.com/你的用户名/你的仓库名.git

# 推送代码
git add .
git commit -m "配置 CI/CD 到个人仓库"
git push origin main
```

---

### 步骤 5：验证 CI/CD 运行（10分钟）

1. 进入你的 GitHub 仓库
2. 点击 **Actions** 标签
3. 应该能看到工作流自动运行
4. 等待构建和部署完成
5. 检查部署日志，确认成功

---

### 步骤 6：验证部署（5分钟）

```bash
# 在服务器上检查服务状态
ssh root@你的服务器IP
cd /opt/campus-trading-platform
docker-compose ps

# 查看日志
docker-compose logs backend
docker-compose logs frontend
```

访问测试：
- 前端：`http://你的服务器IP`
- 后端：`http://你的服务器IP:8080`

---

## 🔍 关键差异点总结

| 项目 | 组长仓库 | 你的仓库 | 是否需要重新配置 |
|------|---------|---------|----------------|
| **服务器** | 同一个 | 同一个 | ❌ 不需要 |
| **SSH 密钥** | 已配置 | 如果相同 | ❌ 不需要（如果不同则需要） |
| **GitHub Secrets** | 组长的仓库 | 你的仓库 | ✅ **必须重新配置** |
| **Docker Hub 用户名** | 组长的 | 你的（可能不同） | ⚠️ **可能需要更新** |
| **服务器 docker-compose.yml** | 组长的镜像名 | 你的镜像名 | ⚠️ **可能需要更新** |
| **代码推送** | 组长的仓库 | 你的仓库 | ✅ **必须推送** |

---

## ⚠️ 常见问题

### Q1: 如果 Docker Hub 用户名和组长一样怎么办？

**A**: 如果使用同一个 Docker Hub 账号：
- GitHub Secrets 中的 `DOCKER_USERNAME` 和 `DOCKER_PASSWORD` 可以使用相同的值
- 服务器上的 `docker-compose.yml` 不需要修改
- 只需要配置 GitHub Secrets 和推送代码即可

### Q2: 如果使用不同的 SSH 密钥怎么办？

**A**: 
1. 生成新的 SSH 密钥对
2. 将公钥添加到服务器：`ssh-copy-id root@你的服务器IP`
3. 将私钥添加到 GitHub Secrets 的 `SSH_KEY`

### Q3: 服务器上已经有组长的容器在运行怎么办？

**A**: 
1. 如果使用**不同的 Docker Hub 用户名**，镜像名称不同，不会冲突
2. 如果使用**相同的 Docker Hub 用户名**，会拉取相同的镜像，但容器名称可能冲突
3. 建议先停止组长的容器（如果不再使用）：
   ```bash
   cd /opt/campus-trading-platform
   docker-compose down
   ```
4. 然后使用你的配置重新启动

### Q4: 如何同时支持两个仓库部署到同一服务器？

**A**: 可以使用不同的容器名称和端口：

在服务器上创建两个不同的目录：
- `/opt/campus-trading-platform-leader`（组长的）
- `/opt/campus-trading-platform-mine`（你的）

或者使用不同的端口：
- 组长的：端口 80, 8080
- 你的：端口 81, 8081

---

## ✅ 最终检查清单

完成以下所有项目后，你的 CI/CD 就配置好了：

- [ ] GitHub Secrets 已配置（DOCKER_USERNAME, DOCKER_PASSWORD, HOST, USERNAME, SSH_KEY）
- [ ] 服务器上的 docker-compose.yml 已更新（如果 Docker Hub 用户名不同）
- [ ] 代码已推送到你的 GitHub 仓库
- [ ] GitHub Actions 工作流文件存在（.github/workflows/ci-cd.yml）
- [ ] GitHub Actions 运行成功
- [ ] 服务部署成功并可以访问

---

**完成以上步骤后，你的个人仓库就可以自动部署到服务器了！** 🚀

