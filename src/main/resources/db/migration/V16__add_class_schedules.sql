-- Thời khóa biểu cho mỗi lớp tín chỉ (Clazz)
CREATE TABLE class_schedules (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    clazz_id     BIGINT       NOT NULL,
    day_of_week  TINYINT      NOT NULL COMMENT '1=Monday, 7=Sunday',
    start_period INT          NOT NULL COMMENT 'Tiết bắt đầu (1-12)',
    end_period   INT          NOT NULL COMMENT 'Tiết kết thúc (1-12)',
    room         VARCHAR(50)  NULL,
    created_at   DATETIME(6)  NULL,
    updated_at   DATETIME(6)  NULL,
    CONSTRAINT fk_schedule_clazz FOREIGN KEY (clazz_id) REFERENCES clazzes(id) ON DELETE CASCADE,
    CONSTRAINT chk_period_range  CHECK (start_period >= 1 AND end_period <= 12 AND start_period <= end_period),
    CONSTRAINT chk_day_of_week   CHECK (day_of_week BETWEEN 1 AND 7),
    INDEX idx_schedule_clazz (clazz_id),
    INDEX idx_schedule_day   (day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
