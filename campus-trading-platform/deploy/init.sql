/*
 校园二手交易平台 - 数据库初始化脚本
 合并了 second_hand_trading.sql 和 fix_membership_type.sql
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sh_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_address`;
CREATE TABLE `sh_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `consignee_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货人姓名',
  `consignee_phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货人手机号',
  `province_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '省',
  `city_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '市',
  `region_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '区',
  `detail_address` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '详细地址',
  `default_flag` tinyint NOT NULL COMMENT '是否默认地址',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_admin
-- ----------------------------
DROP TABLE IF EXISTS `sh_admin`;
CREATE TABLE `sh_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '管理员账号',
  `admin_password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `admin_name` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '管理员名字',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_favorite
-- ----------------------------
DROP TABLE IF EXISTS `sh_favorite`;
CREATE TABLE `sh_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
  `create_time` datetime NOT NULL COMMENT '加入收藏的时间',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物主键id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC, `idle_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收藏信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_idle_item
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item`;
CREATE TABLE `sh_idle_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `idle_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '闲置物名称',
  `idle_details` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '详情',
  `picture_list` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图集',
  `idle_price` decimal(10, 2) NOT NULL COMMENT '价格',
  `idle_place` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发货地区',
  `idle_label` int NOT NULL COMMENT '分类标签',
  `release_time` datetime NOT NULL COMMENT '发布时间',
  `idle_status` tinyint NOT NULL COMMENT '状态（发布1、下架2、删除0）',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `stock` int NOT NULL DEFAULT 1 COMMENT '库存数量（经营性卖家使用，普通用户默认为1）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '二手商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_message
-- ----------------------------
DROP TABLE IF EXISTS `sh_message`;
CREATE TABLE `sh_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置主键id',
  `content` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '留言内容',
  `create_time` datetime NOT NULL COMMENT '留言时间',
  `to_user` bigint NOT NULL COMMENT '所回复的用户',
  `to_message` bigint NULL DEFAULT NULL COMMENT '所回复的留言',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE,
  INDEX `idle_id_index`(`idle_id` ASC) USING BTREE,
  INDEX `to_user_index`(`to_user` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '留言表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `sh_chat_message`;
CREATE TABLE `sh_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `from_user` bigint NOT NULL COMMENT '发送方用户id',
  `to_user` bigint NOT NULL COMMENT '接收方用户id',
  `idle_id` bigint NULL DEFAULT NULL COMMENT '关联的闲置id（可为空）',
  `content` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '发送时间',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读（0-未读；1-已读）',
  `image_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '图片地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `chat_from_user_index`(`from_user` ASC) USING BTREE,
  INDEX `chat_to_user_index`(`to_user` ASC) USING BTREE,
  INDEX `chat_idle_id_index`(`idle_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '站内私信表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_order
-- ----------------------------
DROP TABLE IF EXISTS `sh_order`;
CREATE TABLE `sh_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_number` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物品主键id',
  `order_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `payment_status` tinyint NOT NULL COMMENT '支付状态',
  `payment_way` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `order_status` tinyint NOT NULL COMMENT '订单状态',
  `is_deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_order_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_order_address`;
CREATE TABLE `sh_order_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `consignee_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货人',
  `consignee_phone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话',
  `detail_address` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `orderId`(`order_id` ASC) USING BTREE,
  INDEX `order_id_index`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_after_sale
-- ----------------------------
DROP TABLE IF EXISTS `sh_after_sale`;
CREATE TABLE `sh_after_sale`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID（申请人）',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `application_type` tinyint NOT NULL COMMENT '申请类型：1-质量问题，2-描述不符，3-未收到货，4-其他',
  `problem_description` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '问题描述',
  `evidence_images` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '证据图片（JSON格式）',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `application_status` tinyint NOT NULL DEFAULT 0 COMMENT '申请状态：0-待卖家审核，1-卖家已同意，2-卖家已拒绝，3-已完成退款，4-已取消',
  `seller_review_time` datetime NULL DEFAULT NULL COMMENT '卖家审核时间',
  `seller_review_result` tinyint NULL DEFAULT NULL COMMENT '卖家审核结果：1-同意，2-拒绝',
  `seller_reject_reason` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卖家拒绝原因',
  `seller_evidence_images` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卖家举证图片（JSON格式）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_id_index`(`order_id` ASC) USING BTREE,
  INDEX `buyer_id_index`(`buyer_id` ASC) USING BTREE,
  INDEX `seller_id_index`(`seller_id` ASC) USING BTREE,
  INDEX `status_index`(`application_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '售后申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_dispute
-- ----------------------------

-- ----------------------------
-- Table structure for sh_user
-- ----------------------------
DROP TABLE IF EXISTS `sh_user`;
CREATE TABLE `sh_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账号（手机号）',
  `user_password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录密码',
  `nickname` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '头像',
  `sign_in_time` datetime NOT NULL COMMENT '注册时间',
  `user_status` tinyint NULL DEFAULT NULL COMMENT '状态（1代表封禁）',
  `user_role` tinyint NOT NULL DEFAULT 0 COMMENT '用户角色（0-普通用户，1-经营性卖家，2-管理员）',
  `membership_type` tinyint NOT NULL DEFAULT 0 COMMENT '会员类型（0-普通用户，1-基础会员，2-高级会员）',
  `membership_expire_time` datetime NULL DEFAULT NULL COMMENT '会员到期时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_rating
-- ----------------------------
DROP TABLE IF EXISTS `sh_rating`;
CREATE TABLE `sh_rating`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID（评价人）',
  `seller_id` bigint NOT NULL COMMENT '卖家ID（被评价人）',
  `rating` int NOT NULL COMMENT '评分（1-5分）',
  `comment` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime NOT NULL COMMENT '评价时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_id_unique`(`order_id` ASC) USING BTREE COMMENT '每个订单只能评价一次',
  INDEX `buyer_id_index`(`buyer_id` ASC) USING BTREE,
  INDEX `seller_id_index`(`seller_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_merchant_application
-- ----------------------------
DROP TABLE IF EXISTS `sh_merchant_application`;
CREATE TABLE `sh_merchant_application`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '申请人用户ID',
  `shop_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '店铺名称',
  `business_license` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '营业执照图片URL',
  `id_card_front` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '身份证正面图片URL',
  `id_card_back` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '身份证反面图片URL',
  `contact_phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '联系电话',
  `contact_address` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '联系地址',
  `application_reason` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请理由',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态（0-待审核，1-已通过，2-已拒绝）',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '审核管理员ID',
  `admin_comment` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员审核意见',
  `create_time` datetime NOT NULL COMMENT '申请时间',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id_unique`(`user_id` ASC) USING BTREE COMMENT '每个用户只能有一个申请',
  INDEX `status_index`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商家认证申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_membership_order
-- ----------------------------
DROP TABLE IF EXISTS `sh_membership_order`;
CREATE TABLE `sh_membership_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `membership_type` tinyint NOT NULL COMMENT '会员类型（1-基础会员，2-高级会员）',
  `order_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单号',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `payment_status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态（0-待支付，1-已支付，2-已退款）',
  `payment_way` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `duration_months` int NOT NULL DEFAULT 1 COMMENT '购买时长（月）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_number_unique`(`order_number` ASC) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE,
  INDEX `payment_status_index`(`payment_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_idle_item_pin
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item_pin`;
CREATE TABLE `sh_idle_item_pin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `idle_item_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pin_start_time` datetime NOT NULL COMMENT '置顶开始时间',
  `pin_end_time` datetime NOT NULL COMMENT '置顶结束时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idle_item_id_index`(`idle_item_id` ASC) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE,
  INDEX `pin_time_index`(`pin_start_time` ASC, `pin_end_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品置顶记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 插入示例数据
-- ----------------------------

-- 管理员数据
INSERT INTO `sh_admin` VALUES (1, 'admin', '123456', '超级管理员');

-- 用户数据（简化版）
INSERT INTO `sh_user` VALUES
(1, '13800138000', '123456', '测试用户1', 'http://localhost:8080/image?imageName=default_avatar.jpg', NOW(), 0, 0, 0, NULL),
(2, '13800138001', '123456', '测试用户2', 'http://localhost:8080/image?imageName=default_avatar.jpg', NOW(), 0, 0, 0, NULL),
(3, '13800138002', '123456', '商家用户', 'http://localhost:8080/image?imageName=default_avatar.jpg', NOW(), 0, 1, 0, NULL);

-- 商品数据（简化版）
INSERT INTO `sh_idle_item` VALUES
(1, '二手笔记本电脑', '九成新，配置良好，适合学习办公', '[]', 2000.00, '14号楼', 1, NOW(), 1, 1, 1),
(2, 'Java编程书籍', '经典教材，有笔记', '[]', 50.00, '11号楼', 4, NOW(), 1, 1, 1),
(3, '机械键盘', '黑轴，手感好', '[]', 150.00, '9号楼', 1, NOW(), 1, 2, 1),
(4, '篮球', '八成新，质量好', '[]', 80.00, '14号楼', 3, NOW(), 1, 2, 1),
(5, '水壶', '即将毕业，低价出售', '[]', 15.00, '11号楼', 2, NOW(), 1, 3, 1);

-- 地址数据
INSERT INTO `sh_address` VALUES
(1, '张三', '13800138000', '男生宿舍', '14号楼', '二层', '205宿舍', 1, 1),
(2, '李四', '13800138001', '男生宿舍', '11号楼', '三层', '305宿舍', 1, 2);

-- 收藏数据
INSERT INTO `sh_favorite` VALUES
(1, NOW(), 1, 1),
(2, NOW(), 2, 3);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for sh_auto_reply_template
-- 自动回复模板表
-- ----------------------------
DROP TABLE IF EXISTS `sh_auto_reply_template`;
CREATE TABLE `sh_auto_reply_template`  (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                           `user_id` bigint NOT NULL COMMENT '用户id（卖家）',
                                           `keyword` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关键词（如：自提、价格）',
                                           `reply_content` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '回复内容（如：自提点：3 栋楼下快递柜）',
                                           `is_enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用（0-禁用；1-启用）',
                                           `create_time` datetime NOT NULL COMMENT '创建时间',
                                           `update_time` datetime NOT NULL COMMENT '更新时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `auto_reply_user_id_index`(`user_id` ASC) USING BTREE,
                                           INDEX `auto_reply_keyword_index`(`keyword` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '自动回复模板表' ROW_FORMAT = DYNAMIC;
