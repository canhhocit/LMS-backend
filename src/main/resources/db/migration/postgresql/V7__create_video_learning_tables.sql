-- V7: Create tables for interactive video learning, watch progress, and study notes

CREATE TABLE IF NOT EXISTS video_progress (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
    lesson_id BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    last_watched_seconds NUMERIC(8,2) NOT NULL DEFAULT 0.00,
    max_watched_seconds NUMERIC(8,2) NOT NULL DEFAULT 0.00,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_video_progress_enrollment_lesson UNIQUE (enrollment_id, lesson_id)
);

CREATE TABLE IF NOT EXISTS in_video_quizzes (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    trigger_at_seconds NUMERIC(6,2) NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_option VARCHAR(1) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS student_video_notes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    lesson_id BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    note_text TEXT NOT NULL,
    timestamp_seconds NUMERIC(6,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_video_progress_enrollment ON video_progress(enrollment_id);
CREATE INDEX idx_in_video_quizzes_lesson ON in_video_quizzes(lesson_id);
CREATE INDEX idx_student_video_notes_user_lesson ON student_video_notes(user_id, lesson_id);
