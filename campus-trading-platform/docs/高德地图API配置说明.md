# 高德地图API配置说明

## 1. 申请API Key

1. 访问高德开放平台：https://console.amap.com/
2. 注册/登录账号
3. 进入「应用管理」→「我的应用」
4. 点击「创建新应用」
5. 填写应用信息：
   - 应用名称：校园二手交易平台
   - 应用类型：Web端(JS API)
6. 创建应用后，点击「添加」按钮添加Key
7. 填写Key信息：
   - Key名称：校园二手交易平台Web端
   - 服务平台：Web端(JS API)
   - 白名单：可以设置为 `*`（开发阶段）或具体域名（生产环境）
8. 提交后获得API Key

## 2. 配置API Key

### 方法1：直接修改 index.html（开发环境）

打开 `frontend/public/index.html`，找到：

```html
<script type="text/javascript" src="https://webapi.amap.com/maps?v=2.0&key=YOUR_AMAP_KEY&plugin=AMap.Geocoder,AMap.Driving"></script>
```

将 `YOUR_AMAP_KEY` 替换为你的实际API Key。

### 方法2：使用环境变量（推荐，生产环境）

1. 创建 `.env` 文件（在 `frontend` 目录下）：
```
VUE_APP_AMAP_KEY=你的API_Key
```

2. 修改 `index.html`：
```html
<script type="text/javascript" src="https://webapi.amap.com/maps?v=2.0&key=<%= process.env.VUE_APP_AMAP_KEY %>&plugin=AMap.Geocoder,AMap.Driving"></script>
```

## 3. 功能说明

### 已实现的功能

1. **地址解析（Geocoding）**
   - 将文本地址转换为经纬度坐标
   - 支持卖家位置和买家地址解析

2. **地图标记**
   - 在地图上标记卖家位置（红色标记）
   - 在地图上标记买家位置（蓝色标记）
   - 点击标记显示地址信息

3. **距离计算**
   - 自动计算买卖双方距离
   - 显示距离文本（米/公里）
   - 根据距离建议面交或快递

4. **导航功能**
   - 一键导航到卖家位置
   - 一键导航到买家位置
   - 调用手机高德地图APP进行导航

### 使用场景

- **订单状态为"待发货"或"待收货"时显示**
- **订单已支付**
- **商品有发货地区信息**

## 4. 注意事项

1. **API配额限制**
   - 个人开发者：每天30万次调用
   - 企业开发者：每天100万次调用
   - 建议：对地址解析结果进行缓存，减少API调用

2. **安全性**
   - 生产环境建议设置白名单
   - 不要将API Key提交到公开仓库

3. **费用**
   - 基础功能免费
   - 超出配额后按量计费

## 5. 测试

配置完成后，访问订单详情页（订单状态为待发货或待收货），应该能看到：
- 地图显示区域
- 买卖双方位置标记
- 距离信息
- 导航按钮

如果地图不显示，请检查：
1. API Key是否正确配置
2. 浏览器控制台是否有错误信息
3. 网络是否正常（需要访问高德地图API）

