-- V11__add_semester_to_enrollment.sql
ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS semester VARCHAR(20);
ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS academic_year VARCHAR(20);
