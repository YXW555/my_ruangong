-- H2 数据库初始化脚本（测试用）
-- 注意：使用 MODE=MySQL 模式，语法与 MySQL 兼容

DROP TABLE IF EXISTS `sh_address`;
CREATE TABLE `sh_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `consignee_name` varchar(32) NOT NULL COMMENT '收货人姓名',
  `consignee_phone` varchar(16) NOT NULL COMMENT '收货人手机号',
  `province_name` varchar(32) NOT NULL COMMENT '省',
  `city_name` varchar(32) NOT NULL COMMENT '市',
  `region_name` varchar(32) NOT NULL COMMENT '区',
  `detail_address` varchar(64) NOT NULL COMMENT '详细地址',
  `default_flag` tinyint NOT NULL COMMENT '是否默认地址',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_admin`;
CREATE TABLE `sh_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) NOT NULL COMMENT '管理员账号',
  `admin_password` varchar(16) NOT NULL COMMENT '密码',
  `admin_name` varchar(8) NOT NULL COMMENT '管理员名字',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_favorite`;
CREATE TABLE `sh_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
  `create_time` datetime NOT NULL COMMENT '加入收藏的时间',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物主键id',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_idle_item`;
CREATE TABLE `sh_idle_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `idle_name` varchar(64) NOT NULL COMMENT '闲置物名称',
  `idle_details` varchar(2048) NOT NULL COMMENT '详情',
  `picture_list` varchar(1024) NOT NULL COMMENT '图集',
  `idle_price` decimal(10, 2) NOT NULL COMMENT '价格',
  `idle_place` varchar(32) NOT NULL COMMENT '发货地区',
  `idle_label` int NOT NULL COMMENT '分类标签',
  `release_time` datetime NOT NULL COMMENT '发布时间',
  `idle_status` tinyint NOT NULL COMMENT '状态（发布1、下架2、删除0）',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `stock` int NOT NULL DEFAULT 1 COMMENT '库存数量',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_message`;
CREATE TABLE `sh_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置主键id',
  `content` varchar(256) NOT NULL COMMENT '留言内容',
  `create_time` datetime NOT NULL COMMENT '留言时间',
  `to_user` bigint NOT NULL COMMENT '所回复的用户',
  `to_message` bigint NULL DEFAULT NULL COMMENT '所回复的留言',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_chat_message`;
CREATE TABLE `sh_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `from_user` bigint NOT NULL COMMENT '发送方用户id',
  `to_user` bigint NOT NULL COMMENT '接收方用户id',
  `idle_id` bigint NULL DEFAULT NULL COMMENT '关联的闲置id（可为空）',
  `content` varchar(512) NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '发送时间',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读（0-未读；1-已读）',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_order`;
CREATE TABLE `sh_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_number` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物品主键id',
  `order_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `payment_status` tinyint NOT NULL COMMENT '支付状态',
  `payment_way` varchar(16) NULL DEFAULT NULL COMMENT '支付方式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `order_status` tinyint NOT NULL COMMENT '订单状态',
  `is_deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_order_address`;
CREATE TABLE `sh_order_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `consignee_name` varchar(32) NOT NULL COMMENT '收货人',
  `consignee_phone` varchar(32) NOT NULL COMMENT '电话',
  `detail_address` varchar(128) NOT NULL COMMENT '收货地址',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_after_sale`;
CREATE TABLE `sh_after_sale`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID（申请人）',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `application_type` tinyint NOT NULL COMMENT '申请类型：1-质量问题，2-描述不符，3-未收到货，4-其他',
  `problem_description` varchar(512) NOT NULL COMMENT '问题描述',
  `evidence_images` varchar(1024) NULL DEFAULT NULL COMMENT '证据图片（JSON格式）',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `application_status` tinyint NOT NULL DEFAULT 0 COMMENT '申请状态：0-待卖家审核，1-卖家已同意，2-卖家已拒绝，3-已完成退款，4-已取消',
  `seller_review_time` datetime NULL DEFAULT NULL COMMENT '卖家审核时间',
  `seller_review_result` tinyint NULL DEFAULT NULL COMMENT '卖家审核结果：1-同意，2-拒绝',
  `seller_reject_reason` varchar(256) NULL DEFAULT NULL COMMENT '卖家拒绝原因',
  `seller_evidence_images` varchar(1024) NULL DEFAULT NULL COMMENT '卖家举证图片（JSON格式）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_dispute`;
CREATE TABLE `sh_dispute`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID（买家或卖家）',
  `dispute_type` tinyint NOT NULL COMMENT '纠纷类型',
  `dispute_reason` varchar(512) NOT NULL COMMENT '纠纷原因描述',
  `evidence_images` varchar(1024) NULL DEFAULT NULL COMMENT '证据图片',
  `dispute_status` tinyint NOT NULL DEFAULT 0 COMMENT '纠纷状态',
  `handle_result` tinyint NULL DEFAULT NULL COMMENT '处理结果',
  `handle_description` varchar(512) NULL DEFAULT NULL COMMENT '处理说明',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID（管理员ID）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_user`;
CREATE TABLE `sh_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) NOT NULL COMMENT '账号（手机号）',
  `user_password` varchar(16) NOT NULL COMMENT '登录密码',
  `nickname` varchar(32) NOT NULL COMMENT '昵称',
  `avatar` varchar(256) NOT NULL COMMENT '头像',
  `sign_in_time` datetime NOT NULL COMMENT '注册时间',
  `user_status` tinyint NULL DEFAULT NULL COMMENT '状态（1代表封禁）',
  `user_role` tinyint NOT NULL DEFAULT 0 COMMENT '用户角色',
  `membership_type` tinyint NOT NULL DEFAULT 0 COMMENT '会员类型',
  `membership_expire_time` datetime NULL DEFAULT NULL COMMENT '会员到期时间',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_rating`;
CREATE TABLE `sh_rating`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID（评价人）',
  `seller_id` bigint NOT NULL COMMENT '卖家ID（被评价人）',
  `rating` int NOT NULL COMMENT '评分（1-5分）',
  `comment` varchar(512) NULL DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime NOT NULL COMMENT '评价时间',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_merchant_application`;
CREATE TABLE `sh_merchant_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '申请人用户ID',
  `shop_name` varchar(64) NOT NULL COMMENT '店铺名称',
  `business_license` varchar(256) NULL DEFAULT NULL COMMENT '营业执照图片URL',
  `id_card_front` varchar(256) NOT NULL COMMENT '身份证正面图片URL',
  `id_card_back` varchar(256) NOT NULL COMMENT '身份证反面图片URL',
  `contact_phone` varchar(16) NOT NULL COMMENT '联系电话',
  `contact_address` varchar(128) NOT NULL COMMENT '联系地址',
  `application_reason` varchar(512) NULL DEFAULT NULL COMMENT '申请理由',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '审核管理员ID',
  `admin_comment` varchar(256) NULL DEFAULT NULL COMMENT '管理员审核意见',
  `create_time` datetime NOT NULL COMMENT '申请时间',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_membership_order`;
CREATE TABLE `sh_membership_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `membership_type` tinyint NOT NULL COMMENT '会员类型',
  `order_number` varchar(64) NOT NULL COMMENT '订单号',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `payment_status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态',
  `payment_way` varchar(32) NULL DEFAULT NULL COMMENT '支付方式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `duration_months` int NOT NULL DEFAULT 1 COMMENT '购买时长（月）',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sh_idle_item_pin`;
CREATE TABLE `sh_idle_item_pin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `idle_item_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pin_start_time` datetime NOT NULL COMMENT '置顶开始时间',
  `pin_end_time` datetime NOT NULL COMMENT '置顶结束时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
);
