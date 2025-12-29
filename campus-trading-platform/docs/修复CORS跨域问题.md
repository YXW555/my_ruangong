# 修复 CORS 跨域问题

## 问题

前端运行在 `localhost:8082`，后端运行在 `localhost:8081`，出现 CORS 错误：
```
Access to XMLHttpRequest at 'http://localhost:8081/idle/lable...' from origin 'http://localhost:8082' has been blocked by CORS policy
```

## 原因

后端 CORS 配置可能没有正确允许 `localhost:8082` 的请求。

## 解决方案

### 方案 1：检查并修复后端 CORS 配置

检查 `CorsFilter.java` 和 `CorsConfig.java`，确保允许所有本地开发端口。

### 方案 2：使用代理（推荐）

使用 `vue.config.js` 的代理功能，避免跨域问题。

### 方案 3：修改前端端口

将前端端口改回 8080，与配置一致。

## 立即修复

### 修复 1：使用代理（最简单）

修改 `vue.config.js`，添加代理配置：

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

然后修改 `request.js`，开发环境也使用 `/api`：

```javascript
const baseURL = process.env.VUE_APP_API_BASE_URL || '/api';
```

这样：
- 开发环境：`/api` → 代理到 `localhost:8081`
- 生产环境：`/api` → Nginx 代理到后端
- 不需要处理 CORS

### 修复 2：检查后端 CORS 配置

确保后端允许 `localhost:8082` 的请求。

