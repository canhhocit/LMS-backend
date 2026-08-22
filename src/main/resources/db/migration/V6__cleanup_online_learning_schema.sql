-- V6: Cleanup online learning schema (University LMS migration)
-- Drop SRS Online Learning columns and tables

-- Drop columns from courses table (SRS Online Learning fields)
ALTER TABLE courses DROP COLUMN IF EXISTS mentor_id;
ALTER TABLE courses DROP COLUMN IF EXISTS price;
ALTER TABLE courses DROP COLUMN IF EXISTS status;
ALTER TABLE courses DROP COLUMN IF EXISTS thumbnail;

-- Drop columns from enrollments table (SRS Online Learning fields)
ALTER TABLE enrollments DROP COLUMN IF EXISTS learner_id;
ALTER TABLE enrollments DROP COLUMN IF EXISTS course_id;

-- Drop legacy tables (SRS Online Learning)
DROP TABLE IF EXISTS lesson_progress;
DROP TABLE IF EXISTS mentor_requests;
DROP TABLE IF EXISTS course_reviews;