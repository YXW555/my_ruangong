# 修复 CORS 跨域问题 - 完整方案

## 问题

前端运行在 `localhost:8082`，后端运行在 `localhost:8081`，出现 CORS 错误。

## 已修复

修改了三个 CORS 配置文件：

1. **WebMvcConfig.java**：允许所有本地端口
2. **CorsFilter.java**：允许所有本地端口
3. **CorsConfig.java**：允许所有本地端口

## 需要重新构建后端

修复后需要：

1. **重新构建后端镜像**：
   ```bash
   cd campus-trading-platform/backend
   docker build -t campus-trading-backend:latest .
   ```

2. **或者使用 Maven 重新编译**：
   ```bash
   cd campus-trading-platform/backend
   mvn clean package -DskipTests
   ```

3. **重启后端容器**：
   ```bash
   cd campus-trading-platform
   docker-compose restart backend
   ```

## 验证修复

重启后端后：

1. 刷新前端页面
2. 打开浏览器开发者工具（F12）
3. 查看 Network 标签
4. 应该不再有 CORS 错误
5. 数据应该能正常加载

## 如果还是不行

### 方案 1：使用代理（推荐，避免 CORS）

修改 `vue.config.js`，添加代理：

```javascript
devServer: {
    port: 8080,
    proxy: {
        '/api': {
            target: 'http://localhost:8081',
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

这样前端请求 `/api`，代理会自动转发到 `localhost:8081`，不会有跨域问题。

### 方案 2：检查后端日志

```bash
docker-compose logs backend | tail -30
```

查看是否有 CORS 相关的错误。

