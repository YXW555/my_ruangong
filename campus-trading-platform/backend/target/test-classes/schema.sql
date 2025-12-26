-- H2 数据库初始化脚本（测试用）
-- 注意：使用 MODE=MySQL 模式，语法与 MySQL 兼容

-- 创建用户表
DROP TABLE IF EXISTS sh_user;
CREATE TABLE sh_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  account_number VARCHAR(16) NOT NULL,
  user_password VARCHAR(16) NOT NULL,
  nickname VARCHAR(32) NOT NULL,
  avatar VARCHAR(256) NOT NULL,
  sign_in_time TIMESTAMP NOT NULL,
  user_status TINYINT DEFAULT 0,
  user_role TINYINT NOT NULL DEFAULT 0,
  membership_type TINYINT NOT NULL DEFAULT 0,
  membership_expire_time TIMESTAMP NULL,
  PRIMARY KEY (id)
);

-- 创建唯一索引
CREATE UNIQUE INDEX account_number ON sh_user(account_number);

