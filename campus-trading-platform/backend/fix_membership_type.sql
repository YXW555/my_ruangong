-- 修复 membership_type 字段的数据类型问题
-- 将字符串类型的会员类型转换为数字类型

USE second_hand_trading;

-- 首先检查当前的数据
SELECT id, account_number, membership_type, membership_type_type FROM sh_user;

-- 如果 membership_type 字段是 VARCHAR 类型，需要先修改表结构
-- 添加一个临时字段
ALTER TABLE sh_user ADD COLUMN membership_type_temp TINYINT DEFAULT 0;

-- 将字符串值转换为数字
UPDATE sh_user 
SET membership_type_temp = CASE 
    WHEN membership_type = '普通会员' OR membership_type = '0' OR membership_type IS NULL THEN 0
    WHEN membership_type = '基础会员' OR membership_type = '1' THEN 1
    WHEN membership_type = '高级会员' OR membership_type = '2' THEN 2
    ELSE 0
END;

-- 删除旧字段
ALTER TABLE sh_user DROP COLUMN membership_type;

-- 重命名新字段
ALTER TABLE sh_user CHANGE COLUMN membership_type_temp membership_type TINYINT DEFAULT 0;

-- 验证结果
SELECT id, account_number, membership_type FROM sh_user LIMIT 10;

