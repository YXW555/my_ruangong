-- Patch: Add missing columns required by current application version
-- Run these statements on your MySQL database for the application.
-- IMPORTANT: run on the correct database and backup before applying.
-- Example:
-- mysql -u root -p -h DB_HOST -P DB_PORT YOUR_DB_NAME < deploy/patch_add_columns.sql

-- 1) Add image_url to sh_chat_message (used by chat features)
-- If your MySQL version rejects adding an existing column, check first:
-- SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='sh_chat_message' AND COLUMN_NAME='image_url';
ALTER TABLE sh_chat_message
  ADD COLUMN image_url VARCHAR(1024) NULL DEFAULT NULL COMMENT '消息图片URL';

-- 1b) Ensure sh_message has reply linking columns used by the application
-- Add to_user and to_message if they are missing (link replies)
ALTER TABLE sh_message
  ADD COLUMN to_user BIGINT NULL DEFAULT NULL COMMENT '所回复的用户' ;

ALTER TABLE sh_message
  ADD COLUMN to_message BIGINT NULL DEFAULT NULL COMMENT '所回复的留言';

-- 2) Ensure sh_order contains fields used by order logic
-- Add is_deleted if missing (used to mark soft-deleted orders)
ALTER TABLE sh_order
  ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除';

-- Add finish_time (order completion time) if missing
ALTER TABLE sh_order
  ADD COLUMN finish_time DATETIME NULL DEFAULT NULL COMMENT '订单完成时间';

-- Add fund_status (fund/escrow status) if missing
ALTER TABLE sh_order
  ADD COLUMN fund_status TINYINT(1) NULL DEFAULT NULL COMMENT '资金状态（0-平台担保，1-已释放）';

-- OPTIONAL: verify columns
-- SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT
-- FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME IN ('sh_chat_message', 'sh_order');

