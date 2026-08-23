-- Chương trình đào tạo (CTĐT)
CREATE TABLE curricula (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150)  NOT NULL COMMENT 'Tên CTĐT (VD: CNTT K2024)',
    faculty       VARCHAR(100)  NULL COMMENT 'Khoa quản lý',
    academic_year VARCHAR(20)   NOT NULL,
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    DATETIME(6)   NULL,
    updated_at    DATETIME(6)   NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Môn học trong từng CTĐT
CREATE TABLE curriculum_courses (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    curriculum_id BIGINT       NOT NULL,
    course_id     BIGINT       NOT NULL,
    semester_no   INT          NOT NULL COMMENT 'Học kỳ dự kiến trong CTĐT',
    is_required   BOOLEAN       NOT NULL DEFAULT TRUE COMMENT 'true = bắt buộc, false = tự chọn',
    CONSTRAINT fk_cc_curriculum FOREIGN KEY (curriculum_id) REFERENCES curricula(id) ON DELETE CASCADE,
    CONSTRAINT fk_cc_course     FOREIGN KEY (course_id)     REFERENCES courses(id)   ON DELETE CASCADE,
    CONSTRAINT uk_cc_unique     UNIQUE (curriculum_id, course_id),
    INDEX idx_cc_curriculum (curriculum_id),
    INDEX idx_cc_course     (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Môn tiên quyết (course A yêu cầu course B đã hoàn thành)
CREATE TABLE course_prerequisites (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id           BIGINT NOT NULL COMMENT 'Môn bị yêu cầu phải qua (đăng ký)',
    prerequisite_course_id BIGINT NOT NULL COMMENT 'Môn tiên quyết (phải đạt)',
    CONSTRAINT chk_no_self_prereq CHECK (course_id <> prerequisite_course_id),
    CONSTRAINT fk_cp_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_prereq FOREIGN KEY (prerequisite_course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT uk_cp_pair UNIQUE (course_id, prerequisite_course_id),
    INDEX idx_cp_course (course_id),
    INDEX idx_cp_prereq (prerequisite_course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
