# Redis快速开始指南

## 一、安装Redis（5分钟）

### 最简单方式：Docker

```powershell
# 1. 安装Docker Desktop（如果还没安装）
# 下载地址：https://www.docker.com/products/docker-desktop

# 2. 运行Redis容器
docker run -d -p 6379:6379 --name redis redis:latest

# 3. 验证Redis运行
docker exec -it redis redis-cli ping
# 应该返回：PONG
```

**如果Docker不可用，参考：`docs/Redis安装和配置指南.md`**

---

## 二、验证代码已就绪（1分钟）

### 检查文件是否已创建：

✅ `backend/pom.xml` - 已添加Redis依赖
✅ `backend/src/main/resources/application.properties` - 已添加Redis配置
✅ `backend/src/main/java/.../config/RedisConfig.java` - Redis配置类
✅ `backend/src/main/java/.../utils/RedisUtil.java` - Redis工具类
✅ `backend/src/main/java/.../service/CacheService.java` - 缓存服务

---

## 三、启动测试（2分钟）

### 1. 确保Redis运行
```powershell
docker ps | findstr redis
# 如果没看到，运行：docker start redis
```

### 2. 启动后端服务
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### 3. 检查启动日志
- 如果看到Redis连接成功，说明配置正确
- 如果有连接错误，检查Redis是否启动

---

## 四、功能验证

### 功能1：商品列表缓存

**测试步骤：**
1. 访问商品列表接口：`GET http://localhost:8080/idle/find?page=1&nums=8`
2. 第一次访问：查询数据库（较慢）
3. 5分钟内再次访问：从缓存读取（很快）

**验证方法：**
```powershell
docker exec -it redis redis-cli keys "product:list:*"
# 应该能看到缓存的key
```

---

### 功能2：商品浏览量计数器

**测试步骤：**
1. 访问商品详情：`GET http://localhost:8080/idle/info?id=1`
2. 多次访问，每次浏览量+1

**验证方法：**
```powershell
docker exec -it redis redis-cli get "product:view:1"
# 应该能看到浏览量数字
```

---

### 功能3：分布式锁（订单创建）

**测试步骤：**
1. 同时发起多个订单创建请求（相同商品）
2. 只有一个请求能成功创建订单
3. 其他请求返回"系统繁忙"

**验证方法：**
```powershell
docker exec -it redis redis-cli keys "lock:*"
# 应该能看到锁的key
```

---

## 五、常见问题

### Q1: 启动失败，提示连接Redis失败
**解决：**
```powershell
# 检查Redis是否运行
docker ps | findstr redis

# 如果没运行，启动它
docker start redis

# 测试连接
docker exec -it redis redis-cli ping
```

### Q2: 缓存不生效
**检查：**
1. Redis是否正常运行
2. `application.properties` 中的Redis配置是否正确
3. 查看后端日志是否有Redis相关错误

### Q3: 计数器不增加
**检查：**
1. 商品详情接口是否正常调用
2. Redis中是否有 `product:view:*` 的key
3. 查看代码中是否正确调用了 `redisUtil.increment()`

---

## 六、下一步优化

1. ✅ **已完成**：商品列表缓存
2. ✅ **已完成**：商品详情缓存
3. ✅ **已完成**：商品浏览量计数器
4. ✅ **已完成**：订单创建分布式锁
5. ⏳ **待实现**：支付回调分布式锁
6. ⏳ **待实现**：收藏数计数器
7. ⏳ **待实现**：缓存更新策略

---

## 七、Redis管理工具（可选）

### Redis Desktop Manager
- 下载：https://resp.app/
- 连接：host: localhost, port: 6379
- 可以可视化查看Redis中的数据

---

## 总结

✅ **Redis已集成到项目中**
✅ **三个核心功能已实现**
✅ **可以开始测试使用**

**如果遇到问题，查看：**
- `docs/Redis实现步骤说明.md` - 详细实现说明
- `docs/Redis安装和配置指南.md` - 安装问题排查

