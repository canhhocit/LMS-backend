-- V6: Add Admin Permission RBAC & Curriculum Grading Policy / GPA Scale

-- 1. Admin Permissions
CREATE TABLE admin_permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_admin_permissions (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES admin_permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, permission_id)
);

INSERT INTO admin_permissions (code, description) VALUES
('MANAGE_USERS', 'Quản lý người dùng, sinh viên, giảng viên và lớp hành chính'),
('MANAGE_CURRICULUM', 'Quản lý khóa học, chương trình đào tạo, môn tiên quyết, khoa bộ môn'),
('MANAGE_TUITION', 'Quản lý khung học phí và hóa đơn học phí sinh viên'),
('MANAGE_GRADING_POLICY', 'Quản lý công thức tính điểm và thang quy đổi GPA theo khóa'),
('VIEW_REPORTS', 'Xem báo cáo thống kê, danh sách cảnh báo học vụ, dashboard'),
('MANAGE_REGISTRATION', 'Quản lý các đợt đăng ký học phần'),
('SYSTEM_CONFIG', 'Cấu hình hệ thống và phân quyền Admin');

-- Seed all permissions to existing ADMIN users
INSERT INTO user_admin_permissions (user_id, permission_id)
SELECT u.id, p.id
FROM users u
CROSS JOIN admin_permissions p
WHERE u.role = 'ADMIN';

-- 2. Link User ↔ Curriculum
ALTER TABLE users ADD COLUMN curriculum_id BIGINT REFERENCES curricula(id) ON DELETE SET NULL;

-- 3. Grading Policy per Curriculum
CREATE TABLE grading_policies (
    id BIGSERIAL PRIMARY KEY,
    curriculum_id BIGINT NOT NULL UNIQUE REFERENCES curricula(id) ON DELETE CASCADE,
    attendance_weight NUMERIC(4,3) NOT NULL DEFAULT 0.000,
    midterm_weight NUMERIC(4,3) NOT NULL DEFAULT 0.400,
    final_weight NUMERIC(4,3) NOT NULL DEFAULT 0.600,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. GPA Scale Rules per Curriculum
CREATE TABLE gpa_scale_rules (
    id BIGSERIAL PRIMARY KEY,
    curriculum_id BIGINT NOT NULL REFERENCES curricula(id) ON DELETE CASCADE,
    min_score_10 NUMERIC(4,2) NOT NULL,
    gpa_4 NUMERIC(3,2) NOT NULL,
    sort_order INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gpa_scale_rules_curriculum ON gpa_scale_rules(curriculum_id);

-- Seed default GPA scale rules for all existing curricula
INSERT INTO gpa_scale_rules (curriculum_id, min_score_10, gpa_4, sort_order)
SELECT c.id, r.min_score, r.gpa, r.order_val
FROM curricula c
CROSS JOIN (
    VALUES
        (9.00::numeric, 4.00::numeric, 1),
        (8.50::numeric, 3.70::numeric, 2),
        (8.00::numeric, 3.50::numeric, 3),
        (7.00::numeric, 3.00::numeric, 4),
        (6.50::numeric, 2.50::numeric, 5),
        (5.50::numeric, 2.00::numeric, 6),
        (5.00::numeric, 1.50::numeric, 7),
        (4.00::numeric, 1.00::numeric, 8),
        (0.00::numeric, 0.00::numeric, 9)
) AS r(min_score, gpa, order_val);
