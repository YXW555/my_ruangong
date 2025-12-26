# Redis功能实现步骤说明

## 一、安装Redis（必须先完成）

### 推荐方式：使用Docker（最简单）

1. **安装Docker Desktop**
   - 下载：https://www.docker.com/products/docker-desktop
   - 安装并启动

2. **运行Redis容器**
   ```powershell
   docker run -d -p 6379:6379 --name redis redis:latest
   ```

3. **验证Redis运行**
   ```powershell
   docker exec -it redis redis-cli ping
   # 应该返回 PONG
   ```

### 其他方式
详见：`docs/Redis安装和配置指南.md`

---

## 二、已完成的代码实现

### ✅ 1. 添加依赖
- 已在 `pom.xml` 中添加 `spring-boot-starter-data-redis`

### ✅ 2. 配置文件
- 已在 `application.properties` 中添加Redis配置

### ✅ 3. Redis配置类
- 已创建 `RedisConfig.java` - 配置Redis序列化

### ✅ 4. Redis工具类
- 已创建 `RedisUtil.java` - 封装常用操作
  - 缓存操作（set/get/delete）
  - 计数器操作（increment/decrement）
  - 分布式锁操作（tryLock/releaseLock）

### ✅ 5. 缓存服务
- 已创建 `CacheService.java` - 商品列表和详情缓存

### ✅ 6. 集成到业务代码
- **商品服务**：已集成缓存和浏览量计数器
- **订单服务**：已集成分布式锁（防止重复创建订单）

---

## 三、需要完善的功能

### 1. 支付回调加锁（待实现）

在 `AliPayController` 和 `MembershipController` 的支付回调方法中添加分布式锁。

### 2. 收藏数计数器（待实现）

在收藏/取消收藏时更新Redis计数器。

### 3. 缓存更新策略（待实现）

商品更新/删除时清除相关缓存。

---

## 四、测试步骤

### 1. 启动Redis
```powershell
docker start redis
```

### 2. 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

### 3. 测试缓存功能
- 访问商品列表接口，第一次查询数据库，第二次从缓存读取
- 查看Redis：`docker exec -it redis redis-cli keys "product:*"`

### 4. 测试计数器功能
- 访问商品详情，每次访问浏览量+1
- 查看Redis：`docker exec -it redis redis-cli get "product:view:1"`

### 5. 测试分布式锁
- 同时发起多个订单创建请求，只有一个能成功
- 查看Redis：`docker exec -it redis redis-cli keys "lock:*"`

---

## 五、Redis常用命令（调试用）

```bash
# 连接Redis
docker exec -it redis redis-cli

# 查看所有key
KEYS *

# 查看商品相关key
KEYS product:*

# 查看锁相关key
KEYS lock:*

# 获取某个key的值
GET product:view:1

# 查看key的过期时间
TTL product:detail:1

# 删除某个key
DEL product:detail:1

# 清空所有数据（谨慎使用）
FLUSHDB
```

---

## 六、下一步优化建议

1. **完善支付回调锁**：在支付成功回调中添加分布式锁
2. **收藏数计数器**：实现收藏/取消收藏的计数器
3. **缓存预热**：系统启动时预热热门商品缓存
4. **缓存穿透防护**：对不存在的商品也缓存（防止频繁查询数据库）
5. **缓存雪崩防护**：缓存过期时间添加随机值

---

## 七、注意事项

1. **Redis必须运行**：如果Redis未启动，应用会启动失败
2. **缓存一致性**：商品更新后记得清除缓存
3. **锁超时时间**：根据业务耗时合理设置锁的超时时间
4. **内存管理**：注意Redis内存使用，设置合理的过期时间

---

## 八、故障排查

### Q1: 应用启动失败，提示连接Redis失败
**解决**：
- 检查Redis是否启动：`docker ps | grep redis`
- 检查端口6379是否被占用
- 检查 `application.properties` 中的Redis配置

### Q2: 缓存不生效
**解决**：
- 检查Redis是否正常运行
- 检查 `CacheService` 是否正确注入
- 查看日志是否有异常

### Q3: 分布式锁不生效
**解决**：
- 检查锁的key是否正确
- 检查锁的过期时间是否合理
- 确保在finally中释放锁

---

完成以上步骤后，Redis的三个核心功能就可以正常使用了！

