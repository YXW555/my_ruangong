# 修复 init.sql 目录问题

## 问题现象

- `ls -la init.sql` 显示文件存在
- 但 `head init.sql` 报错：`Is a directory`
- 说明 `init.sql` 是一个**目录**，不是文件

## 立即修复

在服务器上执行：

```bash
cd /opt/campus-trading-platform

# 1. 检查 init.sql 是什么类型
file init.sql
ls -ld init.sql

# 2. 如果确实是目录，删除它
rm -rf init.sql

# 3. 重新上传文件（在本地电脑执行）
# scp deploy/init.sql root@你的服务器IP:/opt/campus-trading-platform/init.sql

# 4. 或者直接在服务器上创建文件
cat > init.sql << 'INIT_EOF'
# 然后粘贴文件内容
INIT_EOF
```

## 快速修复脚本

在服务器上执行：

```bash
cd /opt/campus-trading-platform

# 删除可能存在的目录或文件
rm -rf init.sql

# 验证已删除
ls -la init.sql 2>&1
# 应该显示：No such file or directory

echo "✅ 已清理，现在可以重新上传文件了"
```

然后在本地电脑重新上传：

```bash
# 在本地电脑执行
cd D:\Git\my_ruangong\campus-trading-platform
scp deploy/init.sql root@8.152.208.79:/opt/campus-trading-platform/init.sql
```

## 验证文件是否正确

上传后，在服务器上验证：

```bash
cd /opt/campus-trading-platform

# 1. 检查文件类型（应该是 regular file）
file init.sql
# 应该显示：init.sql: ASCII text

# 2. 检查文件大小（应该约 16KB）
ls -lh init.sql

# 3. 查看文件内容（应该能看到 SQL 语句）
head -10 init.sql
# 应该显示 SQL 代码，而不是错误信息

# 4. 检查文件行数
wc -l init.sql
# 应该显示约 275 行
```

