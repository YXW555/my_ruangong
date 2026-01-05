/*
  Rich sample data for demo purposes.
  Run this after your schema is created to populate more users, items, orders, addresses, favorites and chat messages.
  Note: Adjust IDs if they conflict with existing data in your database.
*/
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- More sample users
-- ----------------------------
INSERT INTO `sh_user` (`id`,`account_number`,`user_password`,`nickname`,`avatar`,`sign_in_time`,`user_status`,`user_role`,`membership_type`,`membership_expire_time`) VALUES
(11,'13700000011','123456','演示用户A','/images/items/avatar_user_1.jpg',CURRENT_TIMESTAMP,0,0,0,NULL),
(12,'13700000012','123456','演示用户B','/images/items/avatar_user_2.jpg',CURRENT_TIMESTAMP,0,0,0,NULL),
(13,'13700000013','123456','演示用户C','/images/items/avatar_user_3.jpg',CURRENT_TIMESTAMP,0,0,1,NULL),
(14,'13700000014','123456','演示用户D','/images/items/avatar_user_1.jpg',CURRENT_TIMESTAMP,0,0,0,NULL),
(15,'13700000015','123456','演示用户E','/images/items/avatar_user_2.jpg',CURRENT_TIMESTAMP,0,0,0,NULL),
(16,'13700000016','123456','演示用户F','/images/items/avatar_user_3.jpg',CURRENT_TIMESTAMP,0,0,0,NULL);

-- ----------------------------
-- More idle items (rich, with multiple images)
-- ----------------------------
INSERT INTO `sh_idle_item` (`id`,`idle_name`,`idle_details`,`picture_list`,`idle_price`,`idle_place`,`idle_label`,`release_time`,`idle_status`,`user_id`,`stock`) VALUES
(1101,'复古单车','二手入门级复古单车，车况良好，适合通勤','[\"/images/items/bike_01.jpg\",\"/images/items/bike_02.jpg\"]',350.00,'校园南门',3,CURRENT_TIMESTAMP,1,11,2),
(1102,'专业耳机（保真）','降噪耳机，成色九成新，配件齐全','[\"/images/items/headphone_01.jpg\",\"/images/items/headphone_02.jpg\"]',420.00,'图书馆东侧',1,CURRENT_TIMESTAMP,1,12,1),
(1103,'漫画书整套','含热门番剧30册，成色良好','[\"/images/items/comics_01.jpg\"]',120.00,'11号楼',4,CURRENT_TIMESTAMP,1,13,5),
(1104,'便携投影仪','1080p 支持，外出学习演示利器','[\"/images/items/projector_01.jpg\"]',680.00,'电子城',1,CURRENT_TIMESTAMP,1,13,1),
(1105,'校园雨伞（新）','耐用抗风，学校定制款','[\"/images/items/umbrella_01.jpg\"]',25.00,'宿舍',2,CURRENT_TIMESTAMP,1,14,10),
(1106,'二手相机（微单）','适合拍照学习/活动记录，镜头成色好','[\"/images/items/camera_01.jpg\",\"/images/items/camera_02.jpg\"]',900.00,'艺术楼',1,CURRENT_TIMESTAMP,1,15,1),
(1107,'考研资料包','全套真题与笔记，适合复习冲刺','[\"/images/items/books_pack_01.jpg\"]',60.00,'教学楼',4,CURRENT_TIMESTAMP,1,11,8),
(1108,'吉他入门套装','民谣吉他+琴包+拨片，适合新手','[\"/images/items/guitar_01.jpg\",\"/images/items/guitar_02.jpg\"]',260.00,'音乐教室',5,CURRENT_TIMESTAMP,1,12,1),
(1109,'运动护具套装','护膝+护腕，健身/运动首选','[\"/images/items/sports_01.jpg\"]',55.00,'体育馆',3,CURRENT_TIMESTAMP,1,14,6),
(1110,'二手平板','适合阅读与做笔记，带保护套','[\"/images/items/tablet_01.jpg\"]',520.00,'图书馆',1,CURRENT_TIMESTAMP,1,15,1);

-- ----------------------------
-- Orders for demo (some paid, some unpaid)
-- ----------------------------
INSERT INTO `sh_order` (`id`,`order_number`,`user_id`,`idle_id`,`order_price`,`payment_status`,`payment_way`,`create_time`,`payment_time`,`order_status`,`is_deleted`,`finish_time`,`fund_status`) VALUES
(3001,'DEMORD001',11,1101,350.00,1,'在线',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1,0,NULL,0),
(3002,'DEMORD002',12,1102,420.00,0,NULL,CURRENT_TIMESTAMP,NULL,0,0,NULL,0),
(3003,'DEMORD003',13,1103,120.00,1,'在线',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,3,0,NULL,0);

-- ----------------------------
-- Order addresses
-- ----------------------------
INSERT INTO `sh_order_address` (`id`,`order_id`,`consignee_name`,`consignee_phone`,`detail_address`) VALUES
(4001,3001,'演示收件人A','13811110001','天津市北辰区北辰校区东区7号楼305'),
(4002,3002,'演示收件人B','13811110002','天津市南开区南开大学学生宿舍2号楼102'),
(4003,3003,'演示收件人C','13811110003','天津市和平区图书馆旁');

-- ----------------------------
-- Favorites (购物车) sample
-- ----------------------------
INSERT INTO `sh_favorite` (`id`,`create_time`,`user_id`,`idle_id`) VALUES
(6001,CURRENT_TIMESTAMP,11,1104),
(6002,CURRENT_TIMESTAMP,12,1107),
(6003,CURRENT_TIMESTAMP,14,1105);

-- ----------------------------
-- Chat messages (with image_url support)
-- ----------------------------
INSERT INTO `sh_chat_message` (`id`,`from_user`,`to_user`,`idle_id`,`content`,`create_time`,`is_read`,`image_url`) VALUES
(7001,11,13,1101,'您好，这辆车还在吗？',CURRENT_TIMESTAMP,0,NULL),
(7002,13,11,1101,'在的，可以看车，约个时间吧',CURRENT_TIMESTAMP,0,NULL),
(7003,12,13,1104,'这个投影仪支持1080p吗？',CURRENT_TIMESTAMP,0,NULL),
(7004,13,12,1104,'支持，请放心购买',CURRENT_TIMESTAMP,0,NULL),
(7005,14,15,1106,'这是产品图片',CURRENT_TIMESTAMP,0,'/images/items/camera_02.jpg');

SET FOREIGN_KEY_CHECKS = 1;

