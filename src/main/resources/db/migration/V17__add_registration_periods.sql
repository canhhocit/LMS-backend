-- Đợt đăng ký học phần
CREATE TABLE registration_periods (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL COMMENT 'Tên đợt (VD: ĐK học kỳ 1 2025-2026)',
    semester      VARCHAR(20)   NOT NULL,
    academic_year VARCHAR(20)   NOT NULL,
    open_at       DATETIME(6)   NOT NULL,
    close_at      DATETIME(6)   NOT NULL,
    max_credits   INT           NULL COMMENT 'Giới hạn tín chỉ/kỳ, NULL = không giới hạn',
    is_active     BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at    DATETIME(6)   NULL,
    updated_at    DATETIME(6)   NULL,
    CONSTRAINT chk_period_window CHECK (close_at > open_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chỉ duy nhất 1 đợt active tại một thời điểm
CREATE UNIQUE INDEX uk_active_period ON registration_periods (is_active) WHERE is_active = TRUE;
