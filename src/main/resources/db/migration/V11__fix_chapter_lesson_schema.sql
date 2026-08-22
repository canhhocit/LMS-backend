-- V11: Fix chapter/lesson schema to match entity mappings
-- Rename class_id to clazz_id in chapters table
ALTER TABLE chapters CHANGE COLUMN class_id clazz_id BIGINT NOT NULL;

-- Add missing columns to lessons table (if not exists)
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS duration INT NOT NULL DEFAULT 0;
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0;

-- Add missing columns to chapters table (if not exists)
ALTER TABLE chapters ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE chapters ADD COLUMN IF NOT EXISTS sort_order INT NOT NULL DEFAULT 0;

</parameter>
