# IDEA 直接运行项目配置

## 你的情况

- ✅ 在 IDEA 中直接运行前端和后端（不是 Docker）
- ✅ Docker Desktop 已打开（可能 MySQL、Redis 在 Docker 中）
- ✅ 前端运行在 8082 端口
- ❓ 后端运行在哪个端口？

## 立即检查

### 第 1 步：检查后端运行端口

在 IDEA 的后端运行控制台中，查找类似这样的日志：
```
Tomcat started on port(s): 8080 (http)
```

或者查看后端启动日志，找到端口信息。

### 第 2 步：检查 Docker 服务

```bash
# 检查 Docker 容器是否运行
docker ps

# 应该能看到 MySQL 和 Redis 容器（如果使用了）
```

### 第 3 步：检查后端配置

查看 `application.properties`：
- `server.port` = 后端运行端口（通常是 8080）
- `spring.datasource.url` = 数据库连接（如果是 `localhost:3307`，说明用 Docker 的 MySQL）
- `spring.redis.host` = Redis 地址（如果是 `localhost`，说明用 Docker 的 Redis）

## 配置方案

### 方案 1：后端运行在 8080（最常见）

如果后端运行在 8080，修改 `vue.config.js`：

```javascript
devServer: {
    port: 8080,  // 如果 8080 被占用，可以改成其他端口
    proxy: {
        '/api': {
            target: 'http://localhost:8080',  // 后端地址
            changeOrigin: true,
            pathRewrite: {
                '^/api': ''
            }
        }
    }
}
```

然后修改 `request.js`：

```javascript
const baseURL = '/api';
```

### 方案 2：后端运行在其他端口

如果后端运行在其他端口（比如 8081），修改 `vue.config.js` 中的 `target`：

```javascript
target: 'http://localhost:你的后端端口',
```

## 快速检查脚本

在 IDEA 的后端运行控制台或终端中执行：

```bash
# Windows PowerShell
netstat -ano | findstr :8080
netstat -ano | findstr :8081
netstat -ano | findstr :8082

# 或者查看 IDEA 的运行配置
# Run → Edit Configurations → 查看后端配置的端口
```

## 推荐配置

### 如果后端运行在 8080

1. **修改 `vue.config.js`**：
   ```javascript
   devServer: {
       port: 8082,  // 保持你现在的端口
       proxy: {
           '/api': {
               target: 'http://localhost:8080',  // 后端地址
               changeOrigin: true,
               pathRewrite: {
                   '^/api': ''
               }
           }
       }
   }
   ```

2. **修改 `request.js`**：
   ```javascript
   const baseURL = '/api';
   ```

3. **重启前端开发服务器**

### 如果后端运行在其他端口

告诉我后端运行在哪个端口，我帮你修改配置。

## 验证

重启前端后：

1. 打开浏览器开发者工具（F12）
2. 查看 Network 标签
3. API 请求应该是：`http://localhost:8082/api/xxx`
4. 应该能正常返回数据

