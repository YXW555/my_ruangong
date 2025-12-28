# 修复前端 API 连接问题

## 问题现象

前端页面可以访问，但数据加载失败，控制台显示：
```
ERR_CONNECTION_REFUSED localhost:8080
```

## 原因

前端代码中硬编码了 `http://localhost:8080` 作为 API 地址，但在生产环境中应该连接到服务器的后端。

## 解决方案

### 方案 1：使用 Nginx 反向代理（已实现，推荐）

1. **创建 nginx.conf 配置文件**
   - 配置 `/api/` 路径代理到后端 `backend:8080`
   - 配置 `/file/` 路径代理到后端文件上传接口

2. **修改前端代码**
   - `request.js`: 使用 `/api` 作为 baseURL（生产环境）
   - 文件上传组件: 使用 `/file/` 作为 action

3. **更新 Dockerfile**
   - 复制 nginx.conf 到容器中

### 方案 2：使用环境变量（备选）

如果不想使用 nginx 代理，可以：

1. 在构建时设置环境变量：
   ```bash
   VUE_APP_API_BASE_URL=http://你的服务器IP:8080 npm run build
   ```

2. 或者在 docker-compose 中设置：
   ```yaml
   frontend:
     environment:
       - VUE_APP_API_BASE_URL=http://你的服务器IP:8080
   ```

## 已修复的文件

1. ✅ `frontend/src/utils/request.js` - 使用 `/api` 前缀（生产环境）
2. ✅ `frontend/src/components/page/me.vue` - 文件上传改为 `/file/`
3. ✅ `frontend/src/components/page/release.vue` - 文件上传改为 `/file/`
4. ✅ `frontend/src/components/page/merchant-manage.vue` - 文件上传改为 `/file/`
5. ✅ `frontend/src/components/page/merchant-apply.vue` - 文件上传改为 `/file/`
6. ✅ `frontend/nginx.conf` - 新增 nginx 配置
7. ✅ `frontend/Dockerfile` - 更新以包含 nginx 配置

## 重新构建和部署

修复后需要重新构建前端镜像：

```bash
# 1. 提交代码
git add .
git commit -m "修复前端 API 连接：使用 nginx 反向代理"
git push origin main

# 2. 等待 GitHub Actions 构建完成

# 3. 在服务器上拉取新镜像并重启
cd /opt/campus-trading-platform
sudo docker compose pull frontend
sudo docker compose up -d frontend
sudo docker compose logs -f frontend
```

## 验证修复

1. 访问前端页面
2. 打开浏览器开发者工具（F12）
3. 查看 Network 标签
4. 应该看到请求发送到 `/api/...` 而不是 `localhost:8080`
5. 数据应该能正常加载

## Nginx 配置说明

nginx.conf 配置了：
- `/api/` → 代理到 `backend:8080/`（所有 API 请求）
- `/file/` → 代理到 `backend:8080/file/`（文件上传）
- `/` → 前端静态文件

这样前端可以通过相对路径访问后端，不需要硬编码 IP 地址。

