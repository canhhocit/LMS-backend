CREATE TABLE lesson_comments (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    parent_id BIGINT REFERENCES lesson_comments(id) ON DELETE CASCADE,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    created_by BIGINT,
    updated_by BIGINT
);