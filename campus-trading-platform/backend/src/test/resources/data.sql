-- H2 测试数据初始化脚本

-- 插入测试用户数据
INSERT INTO sh_user (id, account_number, user_password, nickname, avatar, sign_in_time, user_status, user_role, membership_type, membership_expire_time) 
VALUES (1, '13800138000', '123456', '测试用户', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', CURRENT_TIMESTAMP, 0, 0, 0, NULL);

INSERT INTO sh_user (id, account_number, user_password, nickname, avatar, sign_in_time, user_status, user_role, membership_type, membership_expire_time) 
VALUES (2, '13900139000', '123456', '新用户', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', CURRENT_TIMESTAMP, 0, 0, 0, NULL);

-- 插入测试商品数据
INSERT INTO sh_idle_item (id, user_id, idle_name, idle_details, picture_list, idle_price, idle_place, idle_label, release_time, idle_status, stock)
VALUES (1, 1, '测试商品', '这是一个测试商品', '[]', 99.99, '测试地点', 1, CURRENT_TIMESTAMP, 1, 1);

-- 插入测试订单数据
INSERT INTO sh_order (id, user_id, idle_id, order_number, order_price, payment_status, create_time, order_status)
VALUES (1, 2, 1, 'TESTORDER123', 99.99, 0, CURRENT_TIMESTAMP, 0);
