# 上传 init.sql 文件指南

## 问题排查

如果上传后找不到文件，可能的原因：

1. **上传命令执行失败**（但没有显示错误）
2. **上传到了错误的路径**
3. **文件名不对**
4. **权限问题**

## 正确的上传方法

### 方法 1：使用 SCP 上传（推荐）

**在本地电脑执行**（Windows PowerShell 或 Git Bash）：

```powershell
# 1. 进入项目目录
cd D:\Git\my_ruangong\campus-trading-platform

# 2. 确认文件存在
ls deploy/init.sql

# 3. 上传到服务器（替换为你的服务器IP）
scp deploy/init.sql root@你的服务器IP:/opt/campus-trading-platform/init.sql
```

**上传时会提示输入密码**，输入服务器 root 密码。

### 方法 2：使用 WinSCP（图形界面，更简单）

1. 下载安装 WinSCP
2. 连接到服务器（SSH）
3. 导航到 `/opt/campus-trading-platform`
4. 从本地拖拽 `deploy/init.sql` 到服务器

### 方法 3：直接在服务器上创建文件

如果上传有问题，可以直接在服务器上创建文件：

```bash
# 在服务器上执行
cd /opt/campus-trading-platform

# 使用 cat 命令创建文件（需要复制文件内容）
cat > init.sql << 'EOF'
# 然后粘贴 init.sql 的内容
# 最后按 Ctrl+D 保存
```

## 验证上传是否成功

```bash
# 在服务器上执行
cd /opt/campus-trading-platform

# 1. 检查文件是否存在
ls -la init.sql

# 2. 检查文件大小（应该不是 0）
du -h init.sql

# 3. 查看文件前几行（确认内容正确）
head -20 init.sql
```

## 常见问题

### 问题 1：SCP 命令找不到

**Windows PowerShell**：
- 确保已安装 OpenSSH 客户端
- 或者使用 Git Bash

**Git Bash**：
```bash
scp deploy/init.sql root@你的服务器IP:/opt/campus-trading-platform/init.sql
```

### 问题 2：权限被拒绝

```bash
# 检查服务器目录权限
ls -ld /opt/campus-trading-platform

# 如果需要，创建目录
sudo mkdir -p /opt/campus-trading-platform
sudo chmod 755 /opt/campus-trading-platform
```

### 问题 3：文件上传到了其他位置

```bash
# 在服务器上搜索文件
find / -name "init.sql" 2>/dev/null
```

## 快速验证脚本

在服务器上执行：

```bash
cd /opt/campus-trading-platform

echo "=== 1. 检查文件是否存在 ==="
if [ -f init.sql ]; then
    echo "✅ 文件存在"
    echo "文件大小: $(du -h init.sql | cut -f1)"
    echo "文件权限: $(ls -l init.sql | awk '{print $1, $3, $4}')"
    echo ""
    echo "=== 2. 查看文件前10行 ==="
    head -10 init.sql
else
    echo "❌ 文件不存在"
    echo ""
    echo "请检查："
    echo "1. 上传命令是否执行成功"
    echo "2. 文件路径是否正确"
    echo "3. 是否有权限访问该目录"
fi
```

## 如果还是找不到文件

### 方案 A：重新上传

```bash
# 在本地电脑
cd D:\Git\my_ruangong\campus-trading-platform

# 使用详细模式上传（可以看到进度）
scp -v deploy/init.sql root@你的服务器IP:/opt/campus-trading-platform/init.sql
```

### 方案 B：直接在服务器上创建

```bash
# 在服务器上
cd /opt/campus-trading-platform

# 使用 nano 编辑器创建文件
nano init.sql
# 然后粘贴文件内容，按 Ctrl+X 保存退出
```

### 方案 C：使用 curl 从 GitHub 下载（如果文件已提交到仓库）

```bash
# 在服务器上
cd /opt/campus-trading-platform

# 从 GitHub 下载（需要替换为你的仓库地址）
curl -o init.sql https://raw.githubusercontent.com/你的用户名/你的仓库名/main/campus-trading-platform/deploy/init.sql
```

