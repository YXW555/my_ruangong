# 为什么需要 init.sql？

## 问题解答

### 1. 为什么上传 init.sql 而不是直接上传 second_hand_trading.sql？

**原因**：`docker-compose.prod.yml` 中配置了：

```yaml
volumes:
  - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
```

这意味着：
- MySQL 容器启动时会**自动执行** `/docker-entrypoint-initdb.d/init.sql` 文件
- 这个文件必须命名为 `init.sql`（或者放在这个目录下）
- 如果文件不存在，数据库就不会自动初始化

### 2. 为什么表没有传上去？

**原因**：
1. **MySQL 容器的初始化机制**：
   - MySQL 容器只在**首次启动时**执行 `/docker-entrypoint-initdb.d/` 目录中的 SQL 文件
   - 如果容器已经启动过（即使后来添加了 SQL 文件），也不会自动执行

2. **你的情况**：
   - 容器已经启动过了
   - `init.sql` 文件不存在或没有上传
   - 所以数据库表没有被创建

### 3. 解决方案

我已经帮你：
1. ✅ **合并了两个 SQL 文件**：`second_hand_trading.sql` + `fix_membership_type.sql`
2. ✅ **简化了数据**：只保留少量示例数据
3. ✅ **修复了错误**：修正了 SQL 语法问题
4. ✅ **创建了 `deploy/init.sql`**：可以直接使用

### 4. 如何使用

#### 方法 1：通过 CI/CD 自动部署（推荐）

我已经创建了 `deploy/init.sql`，下次部署时会自动复制到服务器。

#### 方法 2：手动上传（立即修复）

```bash
# 在本地电脑执行
scp campus-trading-platform/deploy/init.sql root@你的服务器IP:/opt/campus-trading-platform/init.sql

# 在服务器上导入
cd /opt/campus-trading-platform
sudo docker compose exec -i mysql mysql -u root -proot123456 second_hand_trading < init.sql
```

#### 方法 3：重新创建 MySQL 容器（会丢失数据）

```bash
# 在服务器上执行
cd /opt/campus-trading-platform

# 停止并删除 MySQL 容器和数据卷
sudo docker compose stop mysql
sudo docker compose rm -f mysql
sudo docker volume rm campus-trading-platform_mysql_data

# 确保 init.sql 存在
ls -la init.sql

# 重新启动（会自动执行 init.sql）
sudo docker compose up -d mysql
```

## 文件说明

- **`backend/second_hand_trading.sql`**：原始完整数据库脚本（包含大量测试数据）
- **`backend/fix_membership_type.sql`**：修复会员类型字段的脚本
- **`deploy/init.sql`**：合并后的简化版本（用于生产环境部署）

## 建议

以后部署时：
1. 使用 `deploy/init.sql`（已合并和简化）
2. 确保文件上传到服务器
3. 如果是新部署，MySQL 会自动执行
4. 如果是已有容器，需要手动导入

