# IDEA 直接运行项目 - 完整配置

## 你的运行方式

- ✅ **前端**：IDEA 中运行 `npm run serve` → `localhost:8082`
- ✅ **后端**：IDEA 中运行 Spring Boot → `localhost:8080`（Spring Boot 默认端口）
- ✅ **MySQL/Redis**：可能在 Docker 中，也可能在本地

## 已修复配置

我已经修改了 `vue.config.js`：
- 前端端口：8082（保持你现在的）
- 代理目标：`http://localhost:8080`（IDEA 中运行的后端）
- 代理路径：`/api` → `localhost:8080`

## 现在需要做的

### 第 1 步：重启前端开发服务器

**必须重启**，让代理配置生效：

```bash
# 在 IDEA 的前端运行终端中
# 1. 停止前端（Ctrl+C）
# 2. 重新启动
npm run serve
```

### 第 2 步：确认后端运行在 8080

在 IDEA 的后端运行控制台中，查找：
```
Tomcat started on port(s): 8080 (http)
```

如果后端运行在其他端口，告诉我，我帮你修改。

### 第 3 步：确认 MySQL 和 Redis

**检查 Docker 容器**：
```bash
docker ps
```

如果看到 MySQL 和 Redis 容器在运行，说明你在用 Docker 的数据库。

**或者检查后端日志**：
- 如果连接成功，说明数据库正常
- 如果连接失败，需要启动 MySQL 和 Redis

## 配置说明

### 当前配置

- **前端**：`localhost:8082`
- **后端**：`localhost:8080`（IDEA 中运行）
- **代理**：`/api` → `localhost:8080`

### 工作流程

1. 前端请求：`/api/idle/lable?...`
2. 代理转发：`http://localhost:8080/idle/lable?...`
3. 后端处理：返回数据
4. 代理返回：给前端

## 验证

重启前端后：

1. 打开浏览器：http://localhost:8082
2. 打开开发者工具（F12）
3. 查看 Network 标签
4. API 请求应该是：`http://localhost:8082/api/xxx`
5. 应该能正常返回数据

## 如果还是不行

### 检查后端端口

在 IDEA 的后端运行控制台中，查找端口信息，或者：

```bash
# Windows PowerShell
netstat -ano | findstr :8080
```

### 检查后端是否运行

访问：http://localhost:8080

如果能看到后端响应（或 404），说明后端在运行。

### 检查代理是否生效

在浏览器 Network 标签中：
- 应该看到请求 URL 是 `localhost:8082/api/xxx`
- 不应该看到 `localhost:8080` 或 `localhost:8081`

## 关于 Docker

**你现在的运行方式**：
- 前端和后端：IDEA 直接运行（不用 Docker）
- MySQL/Redis：可能在 Docker 中（如果 `docker ps` 能看到）

**这样是可以的**：
- 开发环境可以混合使用
- 只要服务能连接就行

**生产环境**：
- 所有服务都在 Docker 中
- 通过 `docker-compose.prod.yml` 部署

## 总结

1. ✅ 已配置代理：`/api` → `localhost:8080`
2. ✅ 不影响生产环境（生产环境用 Nginx 代理）
3. ⚠️ **需要重启前端开发服务器**
4. ⚠️ **确认后端运行在 8080 端口**

重启前端后应该就能正常工作了！

