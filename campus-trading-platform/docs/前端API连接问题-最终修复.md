# 前端 API 连接问题 - 最终修复

## 问题现象

前端页面可以访问，但数据加载失败，控制台显示：
```
ERR_CONNECTION_REFUSED localhost:8080
```

## 原因

前端代码在判断生产环境时可能不准确，导致仍然使用 `localhost:8080` 而不是 `/api`。

## 已修复

修改了 `frontend/src/utils/request.js`，**强制使用 `/api` 作为默认值**，这样：
- 前端请求发送到 `/api/...`
- Nginx 将 `/api/` 代理到 `backend:8080/`
- 不需要依赖环境变量判断

## 需要重新构建和部署

修复后需要：

1. **提交代码**：
   ```bash
   git add .
   git commit -m "修复前端 API 连接：强制使用 /api 前缀"
   git push origin main
   ```

2. **等待 GitHub Actions 完成**（大约 5-10 分钟）

3. **自动部署**：GitHub Actions 会自动：
   - 重新构建前端镜像
   - 推送到 Docker Hub
   - 在服务器上拉取新镜像
   - 重启前端容器

## 验证修复

部署完成后：

1. 刷新前端页面
2. 打开浏览器开发者工具（F12）
3. 查看 Network 标签
4. 应该看到请求发送到 `/api/...` 而不是 `localhost:8080`
5. 数据应该能正常加载

## 如果还是不行

检查服务器上的前端容器：

```bash
# 在服务器上执行
cd /opt/campus-trading-platform

# 1. 检查前端容器是否运行
sudo docker compose ps frontend

# 2. 查看前端日志
sudo docker compose logs --tail=50 frontend

# 3. 检查 nginx 配置是否加载
sudo docker compose exec frontend cat /etc/nginx/conf.d/default.conf

# 4. 测试 nginx 配置
sudo docker compose exec frontend nginx -t

# 5. 重启前端容器
sudo docker compose restart frontend
```

## 快速验证

在浏览器控制台执行：

```javascript
// 检查 baseURL
console.log('API Base URL:', axios.defaults.baseURL);
// 应该显示 "/api" 而不是 "http://localhost:8080"
```

