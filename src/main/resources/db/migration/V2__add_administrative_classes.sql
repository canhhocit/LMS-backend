-- Bảng lớp hành chính (ví dụ: 74DCTT24, 74DCTT25)
CREATE TABLE administrative_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(50) NOT NULL UNIQUE,
    faculty VARCHAR(100),
    academic_year VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_admin_class_name (class_name)
);

-- Thêm FK admin_class_id vào bảng users
ALTER TABLE users ADD COLUMN admin_class_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_user_admin_class
    FOREIGN KEY (admin_class_id) REFERENCES administrative_classes(id) ON DELETE SET NULL;
