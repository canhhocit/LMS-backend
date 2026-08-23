-- Khoa / Bộ môn (Department) chuẩn hóa cho báo cáo theo khoa
CREATE TABLE departments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(20)   NOT NULL,
    name        VARCHAR(150)  NOT NULL,
    description VARCHAR(500)  NULL,
    head_user_id BIGINT       NULL COMMENT 'Trưởng khoa/bộ môn (nullable)',
    is_active   BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  DATETIME(6)   NULL,
    updated_at  DATETIME(6)   NULL,
    CONSTRAINT uk_dept_code UNIQUE (code),
    CONSTRAINT fk_dept_head FOREIGN KEY (head_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_dept_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
