# 阿里云环境变量配置说明

## 方式一：使用启动脚本（推荐）

### Windows PowerShell
```powershell
cd backend
.\start-with-env.ps1
```

### Windows 命令提示符
```cmd
cd backend
start-with-env.bat
```

## 方式二：手动设置环境变量（永久）

### Windows PowerShell（当前会话）
```powershell
$env:ALIYUN_ACCESS_KEY_ID = "LTAI5tCTqqaDCkJUua23F7bb"
$env:ALIYUN_ACCESS_KEY_SECRET = "vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi"
```

### Windows 命令提示符（当前会话）
```cmd
set ALIYUN_ACCESS_KEY_ID=LTAI5tCTqqaDCkJUua23F7bb
set ALIYUN_ACCESS_KEY_SECRET=vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi
```

### Windows 系统环境变量（永久）
1. 右键"此电脑" -> "属性"
2. 点击"高级系统设置"
3. 点击"环境变量"
4. 在"用户变量"或"系统变量"中点击"新建"
5. 添加以下两个变量：
   - 变量名：`ALIYUN_ACCESS_KEY_ID`，变量值：`LTAI5tCTqqaDCkJUua23F7bb`
   - 变量名：`ALIYUN_ACCESS_KEY_SECRET`，变量值：`vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi`
6. 点击"确定"保存
7. 重启IDE或命令行窗口

## 方式三：在IDEA中配置（推荐用于开发）

1. 打开IDEA
2. 运行配置（Run Configuration）
3. 选择你的Spring Boot运行配置
4. 在"Environment variables"中添加：
   - `ALIYUN_ACCESS_KEY_ID=LTAI5tCTqqaDCkJUua23F7bb`
   - `ALIYUN_ACCESS_KEY_SECRET=vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi`

## 验证配置

启动后端服务后，查看控制台输出，应该能看到：
```
[阿里云NLP] 初始化成功，endpoint: nlp.cn-shanghai.aliyuncs.com
[服务初始化] 阿里云NLP可用: true
```

如果看到 `阿里云NLP可用: false`，说明环境变量未正确配置。

## 注意事项

⚠️ **安全提示**：
- 不要将包含AccessKey的脚本提交到Git仓库
- 如果已提交，请立即删除并重新生成AccessKey
- 建议使用系统环境变量或IDEA配置，而不是脚本文件

