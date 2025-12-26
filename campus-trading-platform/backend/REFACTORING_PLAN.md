# 模块化重构计划

## 模块划分方案

### 1. user 模块 - 用户管理
- Controller: UserController
- Service: UserService, UserServiceImpl
- Dao: UserDao
- Entity: UserModel
- Mapper: UserDao.xml

### 2. product 模块 - 商品管理
- Controller: IdleItemController
- Service: IdleItemService, IdleItemServiceImpl
- Dao: IdleItemDao, IdleItemPinDao
- Entity: IdleItemModel, IdleItemPinModel
- Mapper: IdleItemDao.xml, IdleItemPinDao.xml

### 3. order 模块 - 订单管理
- Controller: OrderController, OrderAddressController
- Service: OrderService, OrderServiceImpl, OrderAddressService, OrderAddressServiceImpl
- Dao: OrderDao, OrderAddressDao
- Entity: OrderModel, OrderAddressModel
- Mapper: OrderDao.xml, OrderAddressDao.xml

### 4. payment 模块 - 支付管理
- Controller: AliPayController
- Service: (可能需要创建)
- Config: AliPayConfig

### 5. membership 模块 - 会员管理
- Controller: MembershipController
- Service: MembershipService, MembershipServiceImpl
- Dao: MembershipOrderDao
- Entity: MembershipOrderModel
- Mapper: MembershipOrderDao.xml

### 6. merchant 模块 - 商家管理
- Controller: MerchantController, MerchantApplicationController
- Service: MerchantService, MerchantServiceImpl, MerchantApplicationService, MerchantApplicationServiceImpl
- Dao: MerchantApplicationDao
- Entity: MerchantApplicationModel
- Mapper: MerchantApplicationDao.xml

### 7. message 模块 - 消息管理
- Controller: MessageController, ChatMessageController
- Service: MessageService, MessageServiceImpl, ChatMessageService, ChatMessageServiceImpl
- Dao: MessageDao, ChatMessageDao
- Entity: MessageModel, ChatMessageModel
- Mapper: MessageDao.xml, ChatMessageDao.xml

### 8. rating 模块 - 评价管理
- Controller: RatingController
- Service: RatingService, RatingServiceImpl
- Dao: RatingDao
- Entity: RatingModel
- Mapper: RatingDao.xml

### 9. favorite 模块 - 收藏管理
- Controller: FavoriteController
- Service: FavoriteService, FavoriteServiceImpl
- Dao: FavoriteDao
- Entity: FavoriteModel
- Mapper: FavoriteDao.xml

### 10. address 模块 - 地址管理
- Controller: AddressController
- Service: AddressService, AddressServiceImpl
- Dao: AddressDao
- Entity: AddressModel
- Mapper: AddressDao.xml

### 11. admin 模块 - 后台管理
- Controller: AdminController, StatisticsController
- Service: AdminService, AdminServiceImpl
- Dao: AdminDao
- Entity: AdminModel
- Mapper: AdminDao.xml

### 12. common 模块 - 公共模块
- Config: CorsConfig, AliPayConfig (如果共享)
- DTO: ResultVo, PageVo
- Enums: ErrorMsg
- Exception: ParamException, GlobalExceptionHandler
- Utils: IdFactoryUtil, OrderTask, OrderTaskHandler
- Interceptor: LogCostInterceptor
- Config: WebMvcConfig

### 13. file 模块 - 文件管理
- Controller: FileController
- Service: FileService, FileServiceImpl

## 新的目录结构

```
com.second.hand.trading.server
├── common/              # 公共模块
│   ├── config/         # 配置类
│   ├── dto/            # 数据传输对象
│   ├── enums/          # 枚举类
│   ├── exception/      # 异常处理
│   ├── utils/          # 工具类
│   └── interceptor/    # 拦截器
├── user/               # 用户模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── product/            # 商品模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── order/              # 订单模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── payment/            # 支付模块
│   ├── controller/
│   └── config/
├── membership/         # 会员模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── merchant/          # 商家模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── message/           # 消息模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── rating/            # 评价模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── favorite/          # 收藏模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── address/           # 地址模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
├── admin/             # 后台管理模块
│   ├── controller/
│   ├── service/
│   ├── dao/
│   └── entity/
└── file/              # 文件管理模块
    ├── controller/
    └── service/
```

## 重构步骤

1. 创建新的模块目录结构
2. 移动文件到对应模块
3. 更新包名（package声明）
4. 更新导入语句（import语句）
5. 更新Mapper XML的namespace
6. 测试编译和运行
7. 更新文档

