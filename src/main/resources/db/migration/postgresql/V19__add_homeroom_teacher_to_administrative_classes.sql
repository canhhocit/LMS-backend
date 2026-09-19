-- V19__add_homeroom_teacher_to_administrative_classes.sql
ALTER TABLE administrative_classes ADD COLUMN IF NOT EXISTS homeroom_teacher_id BIGINT REFERENCES users(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_admin_classes_homeroom_teacher ON administrative_classes(homeroom_teacher_id);
