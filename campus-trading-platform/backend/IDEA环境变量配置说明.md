# IDEA中配置阿里云环境变量

## 方法一：在运行配置中设置（推荐）

1. 点击IDEA右上角的运行配置下拉菜单
2. 选择 `Edit Configurations...`
3. 找到你的Spring Boot运行配置（通常是 `ServerApplication`）
4. 在右侧找到 `Environment variables` 选项
5. 点击右侧的文件夹图标或输入框
6. 添加以下两个环境变量：
   ```
   ALIYUN_ACCESS_KEY_ID=LTAI5tCTqqaDCkJUua23F7bb
   ALIYUN_ACCESS_KEY_SECRET=vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi
   ```
7. 点击 `OK` 保存
8. 重新运行应用

## 方法二：使用启动脚本（如果通过命令行启动）

如果你是通过命令行启动，使用启动脚本：
```powershell
cd D:\Git\my_ruangong\campus-trading-platform\backend
.\start-with-env.ps1
```

## 方法三：设置系统环境变量（永久）

1. 右键"此电脑" -> "属性"
2. 点击"高级系统设置"
3. 点击"环境变量"
4. 在"用户变量"中点击"新建"
5. 添加：
   - 变量名：`ALIYUN_ACCESS_KEY_ID`
   - 变量值：`LTAI5tCTqqaDCkJUua23F7bb`
6. 再添加：
   - 变量名：`ALIYUN_ACCESS_KEY_SECRET`
   - 变量值：`vAAPIsx4ekObIaVWZeL5Bb8LSz9JJi`
7. 点击"确定"保存
8. **重启IDEA**（重要！）

## 验证配置

启动应用后，查看控制台输出，应该看到：
```
[阿里云NLP] 初始化成功，endpoint: nlp.cn-shanghai.aliyuncs.com
```

而不是：
```
[阿里云NLP] AccessKey未配置，将使用本地匹配算法
```

