-- 为订单表添加7天售后资金流转相关字段
-- 执行此脚本前请备份数据库

-- 添加订单完成时间字段
ALTER TABLE `sh_order` 
ADD COLUMN `finish_time` datetime NULL DEFAULT NULL COMMENT '订单完成时间（买家确认收货时）' AFTER `order_status`;

-- 添加资金状态字段
ALTER TABLE `sh_order` 
ADD COLUMN `fund_status` tinyint NULL DEFAULT NULL COMMENT '资金状态：0-待释放（平台担保中），1-已释放给卖家' AFTER `finish_time`;

-- 为已完成订单设置默认值（如果有历史数据）
-- 注意：此操作会为所有已完成订单设置完成时间，请根据实际情况调整
-- UPDATE `sh_order` 
-- SET `finish_time` = `payment_time`, 
--     `fund_status` = 0 
-- WHERE `order_status` = 3 
--   AND `payment_status` = 1 
--   AND `finish_time` IS NULL;

