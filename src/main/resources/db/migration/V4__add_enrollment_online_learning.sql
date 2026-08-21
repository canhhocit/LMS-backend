-- =====================================================
-- V4: Add online learning columns to enrollments table
-- This migration adds learner_id and course_id columns for the SRS Online Learning
-- =====================================================

-- Add columns for online learning enrollment
ALTER TABLE enrollments 
ADD COLUMN IF NOT EXISTS learner_id BIGINT,
ADD COLUMN IF NOT EXISTS course_id BIGINT;

ALTER TABLE enrollments 
ADD CONSTRAINT fk_enrollment_learner FOREIGN KEY (learner_id) REFERENCES users(id) ON DELETE SET NULL,
ADD CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL;

-- Create indexes for the new columns
CREATE INDEX IF NOT EXISTS idx_enrollment_learner ON enrollments(learner_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_course ON enrollments(course_id);