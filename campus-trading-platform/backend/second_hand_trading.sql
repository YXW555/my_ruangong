/*
Cleaned schema + sample data for local testing.
Contains essential tables and a small set of rich sample INSERTs (images point to /images/items/*).
Use this file for local full-import if you want a concise, deployment-ready dataset.
*/
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sh_user
-- ----------------------------
DROP TABLE IF EXISTS `sh_user`;
CREATE TABLE `sh_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_number` varchar(16) NOT NULL,
  `user_password` varchar(64) NOT NULL,
  `nickname` varchar(64) NOT NULL,
  `avatar` varchar(256) NOT NULL,
  `sign_in_time` datetime NOT NULL,
  `user_status` tinyint DEFAULT 0,
  `user_role` tinyint NOT NULL DEFAULT 0,
  `membership_type` tinyint NOT NULL DEFAULT 0,
  `membership_expire_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- sample users
INSERT INTO `sh_user` (`id`,`account_number`,`user_password`,`nickname`,`avatar`,`sign_in_time`,`user_status`,`user_role`,`membership_type`) VALUES
(1,'13800138000','123456','测试用户A','/images/items/avatar_user_1.jpg',CURRENT_TIMESTAMP,0,0,0),
(2,'13900139000','123456','测试用户B','/images/items/avatar_user_2.jpg',CURRENT_TIMESTAMP,0,0,0),
(3,'15100000001','123456','商家小刘','/images/items/avatar_user_3.jpg',CURRENT_TIMESTAMP,0,1,0);

-- ----------------------------
-- Table structure for sh_admin
-- ----------------------------
DROP TABLE IF EXISTS `sh_admin`;
CREATE TABLE `sh_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_number` varchar(32) NOT NULL,
  `admin_password` varchar(64) NOT NULL,
  `admin_name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `sh_admin` (`id`,`account_number`,`admin_password`,`admin_name`) VALUES (1,'admin','123456','超级管理员');

-- ----------------------------
-- Table structure for sh_idle_item
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item`;
CREATE TABLE `sh_idle_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `idle_name` varchar(128) NOT NULL,
  `idle_details` text NOT NULL,
  `picture_list` varchar(1024) NOT NULL,
  `idle_price` decimal(10,2) NOT NULL,
  `idle_place` varchar(128) NOT NULL,
  `idle_label` int NOT NULL,
  `release_time` datetime NOT NULL,
  `idle_status` tinyint NOT NULL,
  `user_id` bigint NOT NULL,
  `stock` int NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- sample items (images should exist under backend/src/main/resources/static/images/items/)
INSERT INTO `sh_idle_item` (`id`,`idle_name`,`idle_details`,`picture_list`,`idle_price`,`idle_place`,`idle_label`,`release_time`,`idle_status`,`user_id`,`stock`) VALUES
(1001,'轻薄笔记本','九成新，i5/8GB/256GB，适合学生办公','[\"/images/items/laptop_01.jpg\",\"/images/items/laptop_02.jpg\"]',2200.00,'天津市北辰校区东区图书馆',1,CURRENT_TIMESTAMP,1,3,1),
(1002,'机械键盘（黑轴）','RGB 背光，黑轴，成色95%','[\"/images/items/keyboard_01.jpg\"]',320.00,'天津市北辰校区东区11号楼',1,CURRENT_TIMESTAMP,1,3,1),
(1003,'篮球','专业比赛用球，皮质良好','[\"/images/items/ball_01.jpg\"]',80.00,'操场',3,CURRENT_TIMESTAMP,1,2,5),
(1004,'Java编程书籍（含笔记）','系统学习Java，含大量手写笔记','[\"/images/items/book_java_01.jpg\"]',45.00,'11号楼',4,CURRENT_TIMESTAMP,1,1,3);

-- ----------------------------
-- Table structure for sh_order
-- ----------------------------
DROP TABLE IF EXISTS `sh_order`;
CREATE TABLE `sh_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(64) NOT NULL,
  `user_id` bigint NOT NULL,
  `idle_id` bigint NOT NULL,
  `order_price` decimal(10,2) NOT NULL,
  `payment_status` tinyint NOT NULL,
  `payment_way` varchar(32) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `payment_time` datetime DEFAULT NULL,
  `order_status` tinyint NOT NULL,
  `finish_time` datetime DEFAULT NULL,
  `fund_status` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- sample orders
INSERT INTO `sh_order` (`id`,`order_number`,`user_id`,`idle_id`,`order_price`,`payment_status`,`payment_way`,`create_time`,`payment_time`,`order_status`) VALUES
(2001,'ORD10001',1,1004,45.00,1,'在线',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1),
(2002,'ORD10002',2,1003,80.00,0,NULL,CURRENT_TIMESTAMP,NULL,0);

-- ----------------------------
-- Table structure for sh_order_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_order_address`;
CREATE TABLE `sh_order_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `consignee_name` varchar(64) NOT NULL,
  `consignee_phone` varchar(32) NOT NULL,
  `detail_address` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `sh_order_address` (`id`,`order_id`,`consignee_name`,`consignee_phone`,`detail_address`) VALUES
(3001,2001,'张三','13800000001','天津市北辰区北辰校区东区7号楼305'),
(3002,2002,'李四','13800000002','天津市北辰区北辰校区东区2号楼102');

-- ----------------------------
-- Table structure for sh_favorite
-- ----------------------------
DROP TABLE IF EXISTS `sh_favorite`;
CREATE TABLE `sh_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `user_id` bigint NOT NULL,
  `idle_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `sh_favorite` (`id`,`create_time`,`user_id`,`idle_id`) VALUES
(5001,CURRENT_TIMESTAMP,1,1004);

-- ----------------------------
-- Table structure for sh_message (comments on items)
-- ----------------------------
DROP TABLE IF EXISTS `sh_message`;
CREATE TABLE `sh_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `idle_id` bigint NOT NULL,
  `content` varchar(512) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `sh_message` (`id`,`user_id`,`idle_id`,`content`,`create_time`) VALUES
(4001,1,1004,'这本书还有吗？',CURRENT_TIMESTAMP);

-- ----------------------------
-- Table structure for sh_chat_message (private messages)
-- ----------------------------
DROP TABLE IF EXISTS `sh_chat_message`;
CREATE TABLE `sh_chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_user` bigint NOT NULL,
  `to_user` bigint NOT NULL,
  `idle_id` bigint DEFAULT NULL,
  `content` varchar(1024) NOT NULL,
  `create_time` datetime NOT NULL,
  `is_read` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `sh_chat_message` (`id`,`from_user`,`to_user`,`idle_id`,`content`,`create_time`) VALUES
(6001,1,3,1004,'我可以便宜点卖给你',CURRENT_TIMESTAMP);

-- ----------------------------
-- Table structure for sh_rating
-- ----------------------------
DROP TABLE IF EXISTS `sh_rating`;
CREATE TABLE `sh_rating` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `buyer_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `rating` int NOT NULL,
  `comment` varchar(512) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sh_merchant_application
-- ----------------------------
DROP TABLE IF EXISTS `sh_merchant_application`;
CREATE TABLE `sh_merchant_application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `shop_name` varchar(64) NOT NULL,
  `business_license` varchar(256) DEFAULT NULL,
  `id_card_front` varchar(256) NOT NULL,
  `id_card_back` varchar(256) NOT NULL,
  `contact_phone` varchar(16) NOT NULL,
  `contact_address` varchar(128) NOT NULL,
  `application_reason` varchar(512) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `admin_id` bigint DEFAULT NULL,
  `admin_comment` varchar(256) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `review_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sh_membership_order
-- ----------------------------
DROP TABLE IF EXISTS `sh_membership_order`;
CREATE TABLE `sh_membership_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `membership_type` tinyint NOT NULL,
  `order_number` varchar(64) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_status` tinyint NOT NULL DEFAULT 0,
  `payment_way` varchar(32) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `payment_time` datetime DEFAULT NULL,
  `duration_months` int NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sh_idle_item_pin
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item_pin`;
CREATE TABLE `sh_idle_item_pin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `idle_item_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `pin_start_time` datetime NOT NULL,
  `pin_end_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sh_auto_reply_template
-- ----------------------------
DROP TABLE IF EXISTS `sh_auto_reply_template`;
CREATE TABLE `sh_auto_reply_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `keyword` varchar(64) NOT NULL,
  `reply_content` varchar(512) NOT NULL,
  `is_enabled` tinyint NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
