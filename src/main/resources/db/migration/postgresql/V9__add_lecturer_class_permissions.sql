-- V9: Add lecturer class permissions (resource-scoped permissions for classes)
-- Allows class lecturers to delegate fine-grained permissions to other lecturer users.

-- Catalog of permissions that can be granted within a class
CREATE TABLE IF NOT EXISTS clazz_permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

INSERT INTO clazz_permissions (code, description) VALUES
('MANAGE_CONTENT', 'Quản lý chương, bài học, video của lớp'),
('MANAGE_GRADING', 'Nhập điểm, điểm danh, chấm bài tập/quiz'),
('MANAGE_ANNOUNCEMENT', 'Đăng và sửa thông báo của lớp'),
('MANAGE_FORUM', 'Xoá bài viết/bình luận vi phạm trong diễn đàn của lớp'),
('MANAGE_SCHEDULE', 'Quản lý lịch học của lớp')
ON CONFLICT (code) DO NOTHING;

-- Grants of permissions to specific users within a class
CREATE TABLE IF NOT EXISTS clazz_member_permissions (
    id BIGSERIAL PRIMARY KEY,
    clazz_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES clazz_permissions(id) ON DELETE CASCADE,
    granted_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_clazz_member_permission UNIQUE (clazz_id, user_id, permission_id)
);

CREATE INDEX idx_clazz_member_permissions_lookup ON clazz_member_permissions(clazz_id, user_id);
