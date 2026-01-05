/*
 校园二手交易平台 - 数据库初始化脚本【最终修复完整版】
 修复所有问题：订单创建失败/字段缺失/默认值为空/索引冗余/重复建表/约束不合理
 修复后支持：正常创建订单+支付+私信+收藏+自动回复+会员+商家认证+售后 全部功能
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sh_address 收货地址表
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
                               `default_flag` tinyint NOT NULL COMMENT '是否默认地址(0-否,1-是)',
                               `user_id` bigint NOT NULL COMMENT '用户主键id',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_admin 管理员表
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
-- Table structure for sh_after_sale 售后申请表
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
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `order_id_index`(`order_id` ASC) USING BTREE,
                                  INDEX `buyer_id_index`(`buyer_id` ASC) USING BTREE,
                                  INDEX `seller_id_index`(`seller_id` ASC) USING BTREE,
                                  INDEX `status_index`(`application_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '售后申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_announcement 平台公告表
-- ----------------------------
DROP TABLE IF EXISTS `sh_announcement`;
CREATE TABLE `sh_announcement` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                   `title` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告标题',
                                   `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公告内容（支持HTML）',
                                   `creator_id` bigint NOT NULL COMMENT '发布者用户ID（管理员或经营性卖家）',
                                   `creator_role` tinyint NOT NULL COMMENT '发布者角色（0-用户，1-经营性卖家，2-管理员）',
                                   `is_pinned` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶（1-置顶）',
                                   `pin_time` datetime NULL DEFAULT NULL COMMENT '置顶时间',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='平台公告表';

-- ----------------------------
-- Table structure for sh_auto_reply_template 商家自动回复模板表【修复update_time默认值】
-- ----------------------------
DROP TABLE IF EXISTS `sh_auto_reply_template`;
CREATE TABLE `sh_auto_reply_template`  (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                           `user_id` bigint NOT NULL COMMENT '用户id（卖家）',
                                           `keyword` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关键词（如：自提、价格）',
                                           `reply_content` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '回复内容（如：自提点：3 栋楼下快递柜）',
                                           `is_enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用（0-禁用；1-启用）',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `auto_reply_user_id_index`(`user_id` ASC) USING BTREE,
                                           INDEX `auto_reply_keyword_index`(`keyword` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '自动回复模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_chat_message 站内私信表【修复image_url字段允许为空】
-- ----------------------------
DROP TABLE IF EXISTS `sh_chat_message`;
CREATE TABLE `sh_chat_message`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                    `from_user` bigint NOT NULL COMMENT '发送方用户id',
                                    `to_user` bigint NOT NULL COMMENT '接收方用户id',
                                    `idle_id` bigint NULL DEFAULT NULL COMMENT '关联的闲置id（可为空）',
                                    `content` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息内容',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
                                    `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读（0-未读；1-已读）',
                                    `image_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片地址',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `chat_from_user_index`(`from_user` ASC) USING BTREE,
                                    INDEX `chat_to_user_index`(`to_user` ASC) USING BTREE,
                                    INDEX `chat_idle_id_index`(`idle_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '站内私信表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_favorite 收藏信息表
-- ----------------------------
DROP TABLE IF EXISTS `sh_favorite`;
CREATE TABLE `sh_favorite`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入收藏的时间',
                                `user_id` bigint NOT NULL COMMENT '用户主键id',
                                `idle_id` bigint NOT NULL COMMENT '闲置物主键id',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `user_id`(`user_id` ASC, `idle_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收藏信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_idle_item 二手商品表
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item`;
CREATE TABLE `sh_idle_item`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                 `idle_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '闲置物名称',
                                 `idle_details` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '详情',
                                 `picture_list` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '图集',
                                 `idle_price` decimal(10, 2) NOT NULL COMMENT '价格',
                                 `idle_place` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '发货地区',
                                 `idle_label` int NOT NULL COMMENT '分类标签(1-数码,2-生活用品,3-运动器材,4-书籍资料,5-其他)',
                                 `release_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                                 `idle_status` tinyint NOT NULL COMMENT '状态（发布1、下架2、删除0）',
                                 `user_id` bigint NOT NULL COMMENT '用户主键id',
                                 `stock` int NOT NULL DEFAULT 1 COMMENT '库存数量（经营性卖家使用，普通用户默认为1）',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '二手商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_idle_item_pin 商品置顶记录表
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item_pin`;
CREATE TABLE `sh_idle_item_pin`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                     `idle_item_id` bigint NOT NULL COMMENT '商品ID',
                                     `user_id` bigint NOT NULL COMMENT '用户ID',
                                     `pin_start_time` datetime NOT NULL COMMENT '置顶开始时间',
                                     `pin_end_time` datetime NOT NULL COMMENT '置顶结束时间',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idle_item_id_index`(`idle_item_id` ASC) USING BTREE,
                                     INDEX `user_id_index`(`user_id` ASC) USING BTREE,
                                     INDEX `pin_time_index`(`pin_start_time` ASC, `pin_end_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品置顶记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_merchant_application 商家认证申请表
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
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                                            `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            UNIQUE INDEX `user_id_unique`(`user_id` ASC) USING BTREE COMMENT '每个用户只能有一个申请',
                                            INDEX `status_index`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商家认证申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_message 留言表
-- ----------------------------
DROP TABLE IF EXISTS `sh_message`;
CREATE TABLE `sh_message`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                               `user_id` bigint NOT NULL COMMENT '用户主键id',
                               `idle_id` bigint NOT NULL COMMENT '闲置主键id',
                               `content` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '留言内容',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
                               `to_user` bigint NOT NULL COMMENT '所回复的用户',
                               `to_message` bigint NULL DEFAULT NULL COMMENT '所回复的留言',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `user_id_index`(`user_id` ASC) USING BTREE,
                               INDEX `idle_id_index`(`idle_id` ASC) USING BTREE,
                               INDEX `to_user_index`(`to_user` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '留言表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_membership_order 会员订单表
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
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                                        `duration_months` int NOT NULL DEFAULT 1 COMMENT '购买时长（月）',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `order_number_unique`(`order_number` ASC) USING BTREE,
                                        INDEX `user_id_index`(`user_id` ASC) USING BTREE,
                                        INDEX `payment_status_index`(`payment_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_order 订单主表【核心修复：字段完整+默认值+唯一索引+注释补全】
-- ----------------------------
DROP TABLE IF EXISTS `sh_order`;
CREATE TABLE `sh_order`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                             `order_number` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单编号',
                             `user_id` bigint NOT NULL COMMENT '用户主键id',
                             `idle_id` bigint NOT NULL COMMENT '闲置物品主键id',
                             `order_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
                             `payment_status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态(0-待支付,1-已支付,2-已退款)',
                             `payment_way` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
                             `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态(0-待发货,1-待收货,2-已完成,3-已取消)',
                             `finish_time` datetime NULL DEFAULT NULL COMMENT '订单完成时间（买家确认收货时）',
                             `fund_status` tinyint NULL DEFAULT 0 COMMENT '资金状态：0-待释放，1-已释放给卖家',
                             `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除(0-未删,1-已删)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `idx_order_number`(`order_number`) USING BTREE COMMENT '订单编号唯一防重复',
                             INDEX `idx_user_id`(`user_id`) USING BTREE,
                             INDEX `idx_idle_id`(`idle_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_order_address 订单地址表【核心修复：删除冗余索引+添加外键关联+级联删除】
-- ----------------------------
DROP TABLE IF EXISTS `sh_order_address`;
CREATE TABLE `sh_order_address`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
                                     `order_id` bigint NOT NULL COMMENT '订单id',
                                     `consignee_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货人',
                                     `consignee_phone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话',
                                     `detail_address` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收货地址',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `idx_order_id`(`order_id` ASC) USING BTREE COMMENT '一个订单只能对应一个收货地址',
                                     FOREIGN KEY (`order_id`) REFERENCES `sh_order`(`id`) ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_rating 评价表
-- ----------------------------
DROP TABLE IF EXISTS `sh_rating`;
CREATE TABLE `sh_rating`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                              `order_id` bigint NOT NULL COMMENT '订单ID',
                              `buyer_id` bigint NOT NULL COMMENT '买家ID（评价人）',
                              `seller_id` bigint NOT NULL COMMENT '卖家ID（被评价人）',
                              `rating` int NOT NULL COMMENT '评分（1-5分）',
                              `comment` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评价内容',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `order_id_unique`(`order_id` ASC) USING BTREE COMMENT '每个订单只能评价一次',
                              INDEX `buyer_id_index`(`buyer_id` ASC) USING BTREE,
                              INDEX `seller_id_index`(`seller_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sh_user 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sh_user`;
CREATE TABLE `sh_user`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                            `account_number` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账号（手机号）',
                            `user_password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录密码',
                            `nickname` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
                            `avatar` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '头像',
                            `sign_in_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                            `user_status` tinyint NULL DEFAULT 0 COMMENT '状态（0-正常，1代表封禁）',
                            `user_role` tinyint NOT NULL DEFAULT 0 COMMENT '用户角色（0-普通用户，1-经营性卖家，2-管理员）',
                            `membership_type` tinyint NOT NULL DEFAULT 0 COMMENT '会员类型（0-普通用户，1-基础会员，2-高级会员）',
                            `membership_expire_time` datetime NULL DEFAULT NULL COMMENT '会员到期时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 插入所有示例数据 - 测试账号/商品/地址/收藏/订单/私信/公告 完整数据
-- ----------------------------
-- 管理员数据
INSERT INTO `sh_admin` VALUES (1, 'admin', '123456', '超级管理员');

-- 用户数据
INSERT INTO `sh_user` (`id`,`account_number`,`user_password`,`nickname`,`avatar`,`sign_in_time`,`user_status`,`user_role`,`membership_type`,`membership_expire_time`) VALUES
                                                                                                                                                                          (1,'13800138000','123456','测试用户A','/images/items/avatar_user_1.jpg',NOW(),0,0,0,NULL),
                                                                                                                                                                          (2,'13900139000','123456','测试用户B','/images/items/avatar_user_2.jpg',NOW(),0,0,0,NULL),
                                                                                                                                                                          (3,'15100000001','123456','商家小刘','/images/items/avatar_user_3.jpg',NOW(),0,1,0,NULL),
                                                                                                                                                                          (11,'13700000011','123456','演示用户A','/images/items/avatar_user_1.jpg',NOW(),0,0,0,NULL),
                                                                                                                                                                          (12,'13700000012','123456','演示用户B','/images/items/avatar_user_2.jpg',NOW(),0,0,0,NULL),
                                                                                                                                                                          (13,'13700000013','123456','演示用户C','/images/items/avatar_user_3.jpg',NOW(),0,1,0,NULL),
                                                                                                                                                                          (14,'13700000014','123456','演示用户D','/images/items/avatar_user_1.jpg',NOW(),0,0,0,NULL),
                                                                                                                                                                          (15,'13700000015','123456','演示用户E','/images/items/avatar_user_2.jpg',NOW(),0,0,0,NULL),
                                                                                                                                                                          (16,'13700000016','123456','演示用户F','/images/items/avatar_user_3.jpg',NOW(),0,0,0,NULL);

-- 二手商品数据
INSERT INTO `sh_idle_item` (`id`,`idle_name`,`idle_details`,`picture_list`,`idle_price`,`idle_place`,`idle_label`,`release_time`,`idle_status`,`user_id`,`stock`) VALUES
                                                                                                                                                                      (1001,'轻薄笔记本','九成新，i5/8GB/256GB，适合学生办公','[\"/images/items/laptop_01.jpg\",\"/images/items/laptop_02.jpg\"]',2200.00,'图书馆东侧',1,NOW(),1,3,1),
                                                                                                                                                                      (1002,'机械键盘（黑轴）','RGB 背光，黑轴，成色95%','[\"/images/items/keyboard_01.jpg\",\"/images/items/keyboard_02.jpg\"]',320.00,'11号楼',1,NOW(),1,3,1),
                                                                                                                                                                      (1003,'篮球','专业比赛用球，皮质良好','[\"/images/items/ball_01.jpg\"]',80.00,'操场',3,NOW(),1,2,1),
                                                                                                                                                                      (1004,'Java编程书籍（含笔记）','系统学习Java，含大量手写笔记','[\"/images/items/book_java_01.jpg\",\"/images/items/book_java_02.jpg\"]',45.00,'11号楼',4,NOW(),1,1,3),
                                                                                                                                                                      (1005,'保温水壶','500ml，保温效果良好，健康材质','[\"/images/items/thermos_01.jpg\"]',25.00,'宿舍',2,NOW(),1,2,5),
                                                                                                                                                                      (1006,'二手iPhone','屏幕完好，运行流畅，带原装充电器','[\"/images/items/phone_01.jpg\",\"/images/items/phone_02.jpg\"]',1800.00,'电子城',1,NOW(),1,1,1),
                                                                                                                                                                      (1007,'篮球鞋（耐克）','9成新，限量款','[\"/images/items/shoes_01.jpg\"]',260.00,'校内跳蚤市场',3,NOW(),1,2,1),
                                                                                                                                                                      (1008,'平板电脑','10英寸，适合阅读与笔记','[\"/images/items/tablet_01.jpg\"]',650.00,'图书馆',1,NOW(),1,3,1),
                                                                                                                                                                      (1009,'学习台灯','无极调光，良好护眼','[\"/images/items/lamp_01.jpg\"]',40.00,'宿舍',2,NOW(),1,1,4),
                                                                                                                                                                      (1010,'二手吉他','民谣吉他，带包和备用弦','[\"/images/items/guitar_01.jpg\",\"/images/items/guitar_02.jpg\"]',300.00,'音乐教室',5,NOW(),1,2,1),
                                                                                                                                                                      (1101,'复古单车','二手入门级复古单车，车况良好，适合通勤','[\"/images/items/bike_01.jpg\",\"/images/items/bike_02.jpg\"]',350.00,'校园南门',3,NOW(),1,11,2),
                                                                                                                                                                      (1102,'专业耳机（保真）','降噪耳机，成色九成新，配件齐全','[\"/images/items/headphone_01.jpg\",\"/images/items/headphone_02.jpg\"]',420.00,'图书馆东侧',1,NOW(),1,12,1),
                                                                                                                                                                      (1103,'漫画书整套','含热门番剧30册，成色良好','[\"/images/items/comics_01.jpg\"]',120.00,'11号楼',4,NOW(),1,13,5),
                                                                                                                                                                      (1104,'便携投影仪','1080p 支持，外出学习演示利器','[\"/images/items/projector_01.jpg\"]',680.00,'电子城',1,NOW(),1,13,1),
                                                                                                                                                                      (1105,'校园雨伞（新）','耐用抗风，学校定制款','[\"/images/items/umbrella_01.jpg\"]',25.00,'宿舍',2,NOW(),1,14,10),
                                                                                                                                                                      (1106,'二手相机（微单）','适合拍照学习/活动记录，镜头成色好','[\"/images/items/camera_01.jpg\",\"/images/items/camera_02.jpg\"]',900.00,'艺术楼',1,NOW(),1,15,1),
                                                                                                                                                                      (1107,'考研资料包','全套真题与笔记，适合复习冲刺','[\"/images/items/books_pack_01.jpg\"]',60.00,'教学楼',4,NOW(),1,11,8),
                                                                                                                                                                      (1108,'吉他入门套装','民谣吉他+琴包+拨片，适合新手','[\"/images/items/guitar_01.jpg\",\"/images/items/guitar_02.jpg\"]',260.00,'音乐教室',5,NOW(),1,12,1),
                                                                                                                                                                      (1109,'运动护具套装','护膝+护腕，健身/运动首选','[\"/images/items/sports_01.jpg\"]',55.00,'体育馆',3,NOW(),1,14,6),
                                                                                                                                                                      (1110,'二手平板','适合阅读与做笔记，带保护套','[\"/images/items/tablet_01.jpg\"]',520.00,'图书馆',1,NOW(),1,15,1);

-- 收货地址数据
INSERT INTO `sh_address` (`id`,`consignee_name`,`consignee_phone`,`province_name`,`city_name`,`region_name`,`detail_address`,`default_flag`,`user_id`) VALUES
                                                                                                                                                           (3001,'张三','13800000001','天津市','天津市','北辰区','北辰校区东区7号楼305',1,1),
                                                                                                                                                           (3002,'李四','13800000002','天津市','天津市','北辰区','北辰校区东区2号楼102',1,2);

-- 收藏数据
INSERT INTO `sh_favorite` (`id`,`create_time`,`user_id`,`idle_id`) VALUES
                                                                       (5001,NOW(),1,1004),
                                                                       (5002,NOW(),2,1003),
                                                                       (6001,NOW(),11,1104),
                                                                       (6002,NOW(),12,1107),
                                                                       (6003,NOW(),14,1105);

-- 订单数据
INSERT INTO `sh_order` (`id`,`order_number`,`user_id`,`idle_id`,`order_price`,`payment_status`,`payment_way`,`create_time`,`payment_time`,`order_status`,`finish_time`,`fund_status`,`is_deleted`) VALUES
                                                                                                                                                                                                       (3001,'DEMORD001',11,1101,350.00,1,'在线',NOW(),NOW(),1,NULL,0,0),
                                                                                                                                                                                                       (3002,'DEMORD002',12,1102,420.00,0,NULL,NOW(),NULL,0,NULL,0,0),
                                                                                                                                                                                                       (3003,'DEMORD003',13,1103,120.00,1,'在线',NOW(),NOW(),3,NULL,0,0);

-- 订单地址数据
INSERT INTO `sh_order_address` (`id`,`order_id`,`consignee_name`,`consignee_phone`,`detail_address`) VALUES
                                                                                                         (4001,3001,'演示收件人A','13811110001','天津市北辰区北辰校区东区7号楼305'),
                                                                                                         (4002,3002,'演示收件人B','13811110002','天津市南开区南开大学学生宿舍2号楼102'),
                                                                                                         (4003,3003,'演示收件人C','13811110003','天津市和平区图书馆旁');

-- 私信聊天数据
INSERT INTO `sh_chat_message` (`id`,`from_user`,`to_user`,`idle_id`,`content`,`create_time`,`is_read`,`image_url`) VALUES
                                                                                                                       (7001,11,13,1101,'您好，这辆车还在吗？',NOW(),0,NULL),
                                                                                                                       (7002,13,11,1101,'在的，可以看车，约个时间吧',NOW(),0,NULL),
                                                                                                                       (7003,12,13,1104,'这个投影仪支持1080p吗？',NOW(),0,NULL),
                                                                                                                       (7004,13,12,1104,'支持，请放心购买',NOW(),0,NULL),
                                                                                                                       (7005,14,15,1106,'这是产品图片',NOW(),0,'/images/items/camera_02.jpg');

-- 平台公告数据
INSERT INTO `sh_announcement` (`title`,`content`,`creator_id`,`creator_role`,`is_pinned`,`pin_time`,`create_time`)
VALUES
    ('系统维护通知','平台将于本周日凌晨2:00-4:00进行维护，期间服务可能不稳定，敬请谅解。',1,2,1,NOW(),NOW());

SET FOREIGN_KEY_CHECKS = 1;
