# 模块化重构执行指南

## 快速开始

### 步骤1：创建目录结构

在 `backend/src/main/java/com/second/hand/trading/server/` 下创建以下目录：

```bash
# Windows PowerShell
New-Item -ItemType Directory -Path "user\controller", "user\service", "user\dao", "user\entity"
New-Item -ItemType Directory -Path "product\controller", "product\service", "product\dao", "product\entity"
New-Item -ItemType Directory -Path "order\controller", "order\service", "order\dao", "order\entity"
New-Item -ItemType Directory -Path "payment\controller", "payment\config"
New-Item -ItemType Directory -Path "membership\controller", "membership\service", "membership\dao", "membership\entity"
New-Item -ItemType Directory -Path "merchant\controller", "merchant\service", "merchant\dao", "merchant\entity"
New-Item -ItemType Directory -Path "message\controller", "message\service", "message\dao", "message\entity"
New-Item -ItemType Directory -Path "rating\controller", "rating\service", "rating\dao", "rating\entity"
New-Item -ItemType Directory -Path "favorite\controller", "favorite\service", "favorite\dao", "favorite\entity"
New-Item -ItemType Directory -Path "address\controller", "address\service", "address\dao", "address\entity"
New-Item -ItemType Directory -Path "admin\controller", "admin\service", "admin\dao", "admin\entity"
New-Item -ItemType Directory -Path "file\controller", "file\service"
New-Item -ItemType Directory -Path "common\config", "common\dto", "common\enums", "common\exception", "common\utils", "common\interceptor"
```

### 步骤2：创建Mapper目录结构

在 `backend/src/main/resources/mapper/` 下创建：

```bash
New-Item -ItemType Directory -Path "mapper\user", "mapper\product", "mapper\order", "mapper\payment", "mapper\membership", "mapper\merchant", "mapper\message", "mapper\rating", "mapper\favorite", "mapper\address", "mapper\admin"
```

## 重构顺序

1. **common模块** - 先迁移，因为其他模块都依赖它
2. **user模块** - 基础模块，很多模块依赖它
3. **其他业务模块** - 按依赖关系顺序迁移

## 每个文件的迁移步骤

1. 复制文件到新位置
2. 更新package声明
3. 更新import语句
4. 更新Mapper XML的namespace
5. 移动Mapper XML文件
6. 测试编译

## 注意事项

- 使用IDE的重构功能（如IntelliJ IDEA的Refactor > Move）可以自动更新导入
- 每次迁移一个模块后立即测试
- 使用Git提交每个模块的迁移，便于回滚

