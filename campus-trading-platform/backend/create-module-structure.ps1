# 模块化重构 - 创建目录结构脚本

$basePath = "src\main\java\com\second\hand\trading\server"
$mapperPath = "src\main\resources\mapper"

# 创建业务模块目录
$modules = @(
    "user\controller", "user\service", "user\dao", "user\entity",
    "product\controller", "product\service", "product\dao", "product\entity",
    "order\controller", "order\service", "order\dao", "order\entity",
    "payment\controller", "payment\config",
    "membership\controller", "membership\service", "membership\dao", "membership\entity",
    "merchant\controller", "merchant\service", "merchant\dao", "merchant\entity",
    "message\controller", "message\service", "message\dao", "message\entity",
    "rating\controller", "rating\service", "rating\dao", "rating\entity",
    "favorite\controller", "favorite\service", "favorite\dao", "favorite\entity",
    "address\controller", "address\service", "address\dao", "address\entity",
    "admin\controller", "admin\service", "admin\dao", "admin\entity",
    "file\controller", "file\service",
    "common\config", "common\dto", "common\enums", "common\exception", "common\utils", "common\interceptor"
)

Write-Host "创建Java模块目录..." -ForegroundColor Green
foreach ($module in $modules) {
    $fullPath = Join-Path $basePath $module
    if (-not (Test-Path $fullPath)) {
        New-Item -ItemType Directory -Path $fullPath -Force | Out-Null
        Write-Host "创建: $fullPath" -ForegroundColor Yellow
    } else {
        Write-Host "已存在: $fullPath" -ForegroundColor Gray
    }
}

# 创建Mapper目录
Write-Host "`n创建Mapper目录..." -ForegroundColor Green
$mapperModules = @("user", "product", "order", "membership", "merchant", "message", "rating", "favorite", "address", "admin")
foreach ($module in $mapperModules) {
    $fullPath = Join-Path $mapperPath $module
    if (-not (Test-Path $fullPath)) {
        New-Item -ItemType Directory -Path $fullPath -Force | Out-Null
        Write-Host "创建: $fullPath" -ForegroundColor Yellow
    } else {
        Write-Host "已存在: $fullPath" -ForegroundColor Gray
    }
}

Write-Host "`n目录结构创建完成!" -ForegroundColor Green

