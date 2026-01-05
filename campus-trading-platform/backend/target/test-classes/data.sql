-- H2 测试数据初始化脚本（丰富版，图片使用 /images/items/ 下的静态资源）

-- 插入测试用户数据
INSERT INTO sh_user (id, account_number, user_password, nickname, avatar, sign_in_time, user_status, user_role, membership_type, membership_expire_time) 
VALUES 
  (1, '13800138000', '123456', '测试用户A', '/images/items/avatar_user_1.jpg', CURRENT_TIMESTAMP, 0, 0, 0, NULL),
  (2, '13900139000', '123456', '测试用户B', '/images/items/avatar_user_2.jpg', CURRENT_TIMESTAMP, 0, 0, 0, NULL),
  (3, '15100000001', '123456', '商家小刘', '/images/items/avatar_user_3.jpg', CURRENT_TIMESTAMP, 0, 1, 0, NULL);

-- 插入测试商品数据（每条 picture_list 为 JSON 字符串数组，指向静态目录 /images/items/）
INSERT INTO sh_idle_item (id, user_id, idle_name, idle_details, picture_list, idle_price, idle_place, idle_label, release_time, idle_status, stock)
VALUES
  (1001, 3, '轻薄笔记本', '九成新，i5/8GB/256GB，适合学生办公', '["/images/items/laptop_01.jpg","/images/items/laptop_02.jpg"]', 2200.00, '图书馆东侧', 1, CURRENT_TIMESTAMP, 1, 1),
  (1002, 3, '机械键盘（黑轴）', 'RGB 背光，黑轴，成色95%', '["/images/items/keyboard_01.jpg","/images/items/keyboard_02.jpg"]', 320.00, '11号楼', 1, CURRENT_TIMESTAMP, 1, 1),
  (1003, 2, '篮球', '专业比赛用球，皮质良好', '["/images/items/ball_01.jpg"]', 80.00, '操场', 3, CURRENT_TIMESTAMP, 1, 1),
  (1004, 1, 'Java编程书籍（含笔记）', '系统学习Java，含大量手写笔记', '["/images/items/book_java_01.jpg","/images/items/book_java_02.jpg"]', 45.00, '11号楼', 4, CURRENT_TIMESTAMP, 1, 3),
  (1005, 2, '保温水壶', '500ml，保温效果良好，健康材质', '["/images/items/thermos_01.jpg"]', 25.00, '宿舍', 2, CURRENT_TIMESTAMP, 1, 5),
  (1006, 1, '二手iPhone', '屏幕完好，运行流畅，带原装充电器', '["/images/items/phone_01.jpg","/images/items/phone_02.jpg"]', 1800.00, '电子城', 1, CURRENT_TIMESTAMP, 1, 1),
  (1007, 2, '篮球鞋（耐克）', '9成新，限量款', '["/images/items/shoes_01.jpg"]', 260.00, '校内跳蚤市场', 3, CURRENT_TIMESTAMP, 1, 1),
  (1008, 3, '平板电脑', '10英寸，适合阅读与笔记', '["/images/items/tablet_01.jpg"]', 650.00, '图书馆', 1, CURRENT_TIMESTAMP, 1, 1),
  (1009, 1, '学习台灯', '无极调光，良好护眼', '["/images/items/lamp_01.jpg"]', 40.00, '宿舍', 2, CURRENT_TIMESTAMP, 1, 4),
  (1010, 2, '二手吉他', '民谣吉他，带包和备用弦', '["/images/items/guitar_01.jpg","/images/items/guitar_02.jpg"]', 300.00, '音乐教室', 5, CURRENT_TIMESTAMP, 1, 1);

-- 插入测试订单数据示例
INSERT INTO sh_order (id, order_number, user_id, idle_id, order_price, payment_status, payment_way, create_time, payment_time, order_status, is_deleted)
VALUES
  (2001, 'ORD10001', 1, 1004, 45.00, 1, '在线', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
  (2002, 'ORD10002', 2, 1003, 80.00, 0, NULL, CURRENT_TIMESTAMP, NULL, 0, 0);

-- 地址示例（收货）
INSERT INTO sh_address (id, consignee_name, consignee_phone, province_name, city_name, region_name, detail_address, default_flag, user_id)
VALUES
  (3001, '张三', '13800000001', '天津市', '天津市', '北辰区', '北辰校区东区7号楼305', 1, 1),
  (3002, '李四', '13800000002', '天津市', '天津市', '北辰区', '北辰校区东区2号楼102', 1, 2);
