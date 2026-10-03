-- Add is_grade_locked to classes table
ALTER TABLE classes ADD COLUMN is_grade_locked BOOLEAN DEFAULT FALSE;
