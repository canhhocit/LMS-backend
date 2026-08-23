-- V20: Thêm cờ is_retake cho Enrollment (phân biệt lần học đầu vs học lại)
ALTER TABLE enrollments
    ADD COLUMN is_retake BOOLEAN NOT NULL DEFAULT FALSE
        COMMENT 'True nếu sinh viên đăng ký học lại môn đã trượt';

-- V20: Tuition module (Mục 4)
CREATE TABLE tuition_rates (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    academic_year VARCHAR(20)  NOT NULL COMMENT 'Năm học áp dụng, VD: 2025-2026',
    price_per_credit DECIMAL(12,2) NOT NULL COMMENT 'Đơn giá mỗi tín chỉ (VNĐ)',
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    DATETIME(6)   NULL,
    updated_at    DATETIME(6)   NULL,
    UNIQUE KEY uk_tuition_year (academic_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tuition_invoices (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id    BIGINT        NOT NULL,
    semester      VARCHAR(20)   NOT NULL,
    academic_year VARCHAR(20)   NOT NULL,
    total_credits INT           NOT NULL,
    price_per_credit DECIMAL(12,2) NOT NULL,
    amount        DECIMAL(14,2) NOT NULL COMMENT 'Tổng tiền = total_credits * price_per_credit',
    status        VARCHAR(20)   NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID | PAID',
    paid_at       DATETIME(6)   NULL,
    created_at    DATETIME(6)   NULL,
    updated_at    DATETIME(6)   NULL,
    CONSTRAINT fk_tuition_student FOREIGN KEY (student_id) REFERENCES users (id),
    UNIQUE KEY uk_tuition_invoice (student_id, semester, academic_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tuition_status ON tuition_invoices (status);

-- V20: Thay partial unique index MySQL không hỗ trợ bằng generated column
-- V17 cũ: WHERE is_active=TRUE không chạy được trên MySQL 8
ALTER TABLE registration_periods
    DROP INDEX uk_active_period,
    ADD COLUMN active_flag TINYINT GENERATED ALWAYS AS (CASE WHEN is_active = TRUE THEN 1 ELSE NULL END) STORED,
    ADD UNIQUE KEY uk_active_period_new (active_flag);
