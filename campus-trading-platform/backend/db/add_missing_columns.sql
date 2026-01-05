-- Patch: add_missing_columns.sql
-- Purpose: Add missing columns required by updated code/queries.
-- NOTE: Run this against your MySQL database (backup first).

-- 1) Add image_url to chat messages (used by ChatMessageDao)
ALTER TABLE sh_chat_message
    ADD COLUMN IF NOT EXISTS image_url VARCHAR(1024) NULL DEFAULT NULL;

-- 2) Ensure sh_order has is_deleted, finish_time, fund_status required by code
ALTER TABLE sh_order
    ADD COLUMN IF NOT EXISTS is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS finish_time DATETIME NULL DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS fund_status TINYINT(1) NULL DEFAULT NULL;

-- End of patch

