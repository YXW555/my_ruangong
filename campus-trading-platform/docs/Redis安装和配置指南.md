# Redis安装和配置指南（Windows）

## 一、安装Redis

### 方法一：使用WSL（推荐，性能好）

1. **安装WSL2**
   ```powershell
   # 以管理员身份运行PowerShell
   wsl --install
   ```

2. **在WSL中安装Redis**
   ```bash
   # 进入WSL
   wsl
   
   # 更新包管理器
   sudo apt update
   
   # 安装Redis
   sudo apt install redis-server
   
   # 启动Redis
   sudo service redis-server start
   
   # 测试连接
   redis-cli ping
   # 应该返回 PONG
   ```

3. **配置Redis允许外部访问**
   ```bash
   # 编辑配置文件
   sudo nano /etc/redis/redis.conf
   
   # 找到 bind 127.0.0.1，改为：
   bind 0.0.0.0
   
   # 重启Redis
   sudo service redis-server restart
   ```

### 方法二：使用Docker（简单）

1. **安装Docker Desktop**
   - 下载：https://www.docker.com/products/docker-desktop
   - 安装并启动Docker Desktop

2. **运行Redis容器**
   ```powershell
   docker run -d -p 6379:6379 --name redis redis:latest
   ```

3. **测试连接**
   ```powershell
   docker exec -it redis redis-cli ping
   ```

### 方法三：直接下载Windows版本（最简单）

1. **下载Redis for Windows**
   - 下载地址：https://github.com/microsoftarchive/redis/releases
   - 或使用：https://github.com/tporadowski/redis/releases
   - 下载 `Redis-x64-*.zip`

2. **解压并运行**
   ```powershell
   # 解压到 D:\Redis
   # 运行
   D:\Redis\redis-server.exe
   ```

3. **测试连接**
   ```powershell
   # 新开一个命令行窗口
   D:\Redis\redis-cli.exe ping
   ```

## 二、验证Redis是否运行

### 方法1：命令行测试
```powershell
# 如果使用WSL
wsl redis-cli ping

# 如果使用Docker
docker exec -it redis redis-cli ping

# 如果使用Windows版本
D:\Redis\redis-cli.exe ping
```

### 方法2：使用Redis Desktop Manager（可视化工具，可选）

1. 下载：https://resp.app/ 或 https://github.com/uglide/RedisDesktopManager
2. 连接：host: localhost, port: 6379

## 三、常见问题

### Q1: 端口6379被占用
**解决**：修改Redis端口或关闭占用端口的程序
```bash
# 查看端口占用
netstat -ano | findstr :6379
```

### Q2: 无法连接Redis
**解决**：
- 检查Redis是否启动
- 检查防火墙设置
- 检查端口是否正确

### Q3: WSL中Redis无法从Windows访问
**解决**：需要配置WSL网络或使用localhost

## 四、推荐方案

**开发环境**：推荐使用Docker，简单快速
**生产环境**：使用Linux服务器安装Redis

---

安装完成后，继续下一步：添加依赖和配置

