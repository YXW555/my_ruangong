# IDEA 运行项目 - 最终配置

## 确认信息

从后端日志看：
- ✅ 后端运行在：`localhost:8080`
- ✅ 前端运行在：`localhost:8082`
- ✅ 后端已成功启动

## 当前配置

### vue.config.js
```javascript
devServer: {
    port: 8082,
    proxy: {
        '/api': {
            target: 'http://localhost:8080',
            changeOrigin: true,
            pathRewrite: {
                '^/api': ''
            }
        }
    }
}
```

### request.js
```javascript
const baseURL = '/api';
```

## 必须重启前端

**重要**：修改了 `vue.config.js` 后，**必须重启前端开发服务器**才能生效。

### 重启步骤

1. **在 IDEA 的前端运行终端中**：
   - 按 `Ctrl+C` 停止前端
   - 重新运行 `npm run serve`

2. **或者**：
   - 在 IDEA 中停止前端运行配置
   - 重新启动前端运行配置

## 验证配置

重启前端后：

1. **打开浏览器**：http://localhost:8082
2. **打开开发者工具（F12）**
3. **查看 Network 标签**
4. **应该看到**：
   - 请求 URL：`http://localhost:8082/api/idle/lable?...`
   - 状态码：200（成功）或 404（接口不存在，但说明代理工作了）

## 如果还是不行

### 检查 1：确认代理是否生效

在浏览器 Network 标签中：
- ✅ 应该看到：`localhost:8082/api/xxx`
- ❌ 不应该看到：`localhost:8080` 或 `localhost:8081`

如果还是看到 `localhost:8080`，说明代理没有生效，需要重启前端。

### 检查 2：测试后端接口

直接访问后端接口，确认后端正常：

```bash
# 在浏览器中访问
http://localhost:8080/idle/lable?idleLabel=1&page=1&nums=8
```

如果这个能返回数据，说明后端正常。

### 检查 3：清除浏览器缓存

1. 按 `Ctrl+Shift+R` 硬刷新
2. 或者清除浏览器缓存

## 总结

1. ✅ 后端运行在 8080（已确认）
2. ✅ 前端运行在 8082（已确认）
3. ✅ 代理配置已设置（`/api` → `localhost:8080`）
4. ⚠️ **必须重启前端开发服务器**

重启前端后应该就能正常工作了！

