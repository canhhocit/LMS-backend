-- V25: Seed full realistic data for testing LMS features
-- This migration provides diverse data across multiple students, classes, and semesters.

-- ──────────────────────────────────────────────
-- 1. ADD NEW USERS (Real Vietnamese Names)
-- ──────────────────────────────────────────────
INSERT INTO users (
    email, password, full_name, date_of_birth, role, student_code,
    faculty, major, cohort_year, is_first_login, status, admin_class_id
)
VALUES
    -- K65A CNTT
    ('sv20240101@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Đinh Tùng Lâm', '2006-03-12', 'STUDENT', 'SV20240101', 'Công nghệ thông tin', 'Kỹ thuật phần mềm', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240102@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Hoàng Mai Phương', '2006-07-21', 'STUDENT', 'SV20240102', 'Công nghệ thông tin', 'Kỹ thuật phần mềm', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240103@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Lê Tuấn Kiệt', '2006-01-05', 'STUDENT', 'SV20240103', 'Công nghệ thông tin', 'Kỹ thuật phần mềm', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240104@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Ngô Thanh Hà', '2006-11-30', 'STUDENT', 'SV20240104', 'Công nghệ thông tin', 'Kỹ thuật phần mềm', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240105@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Vũ Đức Duy', '2006-09-15', 'STUDENT', 'SV20240105', 'Công nghệ thông tin', 'Kỹ thuật phần mềm', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    
    -- K65B CNTT
    ('sv20240201@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Trần Thảo My', '2006-02-28', 'STUDENT', 'SV20240201', 'Công nghệ thông tin', 'Hệ thống thông tin', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240202@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Phan Văn Trị', '2006-12-11', 'STUDENT', 'SV20240202', 'Công nghệ thông tin', 'Hệ thống thông tin', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240203@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Lý Quốc Hùng', '2006-04-04', 'STUDENT', 'SV20240203', 'Công nghệ thông tin', 'Hệ thống thông tin', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240204@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Bùi Nhật Minh', '2006-08-19', 'STUDENT', 'SV20240204', 'Công nghệ thông tin', 'Hệ thống thông tin', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240205@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Đặng Thùy Dương', '2006-10-22', 'STUDENT', 'SV20240205', 'Công nghệ thông tin', 'Hệ thống thông tin', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),

    -- K65A QTKD
    ('sv20240301@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Trịnh Bảo Ngọc', '2006-05-17', 'STUDENT', 'SV20240301', 'Quản trị kinh doanh', 'Marketing', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'QTKD-K65A')),
    ('sv20240302@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Đỗ Quang Dũng', '2006-06-25', 'STUDENT', 'SV20240302', 'Quản trị kinh doanh', 'Marketing', 2024, FALSE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'QTKD-K65A'))
ON CONFLICT (email) DO NOTHING;

-- ──────────────────────────────────────────────
-- 2. ADD MORE CLASSES (Including a past semester)
-- ──────────────────────────────────────────────
INSERT INTO classes (class_code, class_name, semester_id, course_id, lecturer_id, max_students)
VALUES
    -- Các lớp học kỳ trước (để test tính năng xem điểm/lịch sử học tập)
    ('IT101-02-2025B', 'Nhập môn lập trình - Nhóm 02', (SELECT id FROM academic_semesters WHERE code = '2025-2026-2'), (SELECT id FROM courses WHERE code = 'IT101'), (SELECT id FROM users WHERE lecturer_code = 'GV002'), 40),
    ('GEN101-02-2025B', 'Kỹ năng học đại học - Nhóm 02', (SELECT id FROM academic_semesters WHERE code = '2025-2026-2'), (SELECT id FROM courses WHERE code = 'GEN101'), (SELECT id FROM users WHERE lecturer_code = 'GV003'), 50),
    
    -- Các lớp học kỳ hiện tại (bổ sung thêm nhóm 2)
    ('IT202-02-2026A', 'Cơ sở dữ liệu - Nhóm 02', (SELECT id FROM academic_semesters WHERE code = '2026-2027-1'), (SELECT id FROM courses WHERE code = 'IT202'), (SELECT id FROM users WHERE lecturer_code = 'GV001'), 40),
    ('IT303-02-2026A', 'Công nghệ phần mềm - Nhóm 02', (SELECT id FROM academic_semesters WHERE code = '2026-2027-1'), (SELECT id FROM courses WHERE code = 'IT303'), (SELECT id FROM users WHERE lecturer_code = 'GV002'), 35)
ON CONFLICT (class_code) DO NOTHING;

-- ──────────────────────────────────────────────
-- 3. ADD GRADING POLICIES
-- ──────────────────────────────────────────────
INSERT INTO grading_policies (class_id, midterm_weight, final_weight)
SELECT id, 0.4, 0.6 FROM classes
WHERE class_code IN ('IT101-01-2026A', 'IT202-01-2026A', 'IT303-01-2026A', 'IT101-02-2025B', 'IT202-02-2026A', 'IT303-02-2026A')
ON CONFLICT (class_id) DO NOTHING;

INSERT INTO grading_policies (class_id, midterm_weight, final_weight)
SELECT id, 0.5, 0.5 FROM classes
WHERE class_code IN ('BUS101-01-2026A', 'GEN101-01-2026A', 'GEN101-02-2025B')
ON CONFLICT (class_id) DO NOTHING;

-- ──────────────────────────────────────────────
-- 4. ADD ENROLLMENTS
-- ──────────────────────────────────────────────
INSERT INTO enrollments (student_id, class_id, status, is_retake)
SELECT u.id, c.id, v.status, v.is_retake
FROM (VALUES
    -- K65A CNTT enrolls in IT101, IT202 (HK1 2026)
    ('sv20240101@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240101@student.edu.vn', 'IT202-01-2026A', 'ACTIVE', FALSE),
    ('sv20240101@student.edu.vn', 'IT303-01-2026A', 'ACTIVE', FALSE),
    
    ('sv20240102@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240102@student.edu.vn', 'IT202-01-2026A', 'ACTIVE', FALSE),
    
    ('sv20240103@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240104@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240105@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    
    -- K65B CNTT enrolls in Nhóm 2 (HK1 2026)
    ('sv20240201@student.edu.vn', 'IT202-02-2026A', 'ACTIVE', FALSE),
    ('sv20240201@student.edu.vn', 'IT303-02-2026A', 'ACTIVE', FALSE),
    ('sv20240202@student.edu.vn', 'IT202-02-2026A', 'ACTIVE', FALSE),
    ('sv20240203@student.edu.vn', 'IT202-02-2026A', 'ACTIVE', FALSE),
    ('sv20240204@student.edu.vn', 'IT202-02-2026A', 'ACTIVE', FALSE),
    ('sv20240205@student.edu.vn', 'IT303-02-2026A', 'ACTIVE', FALSE),

    -- QTKD enrolls in BUS101
    ('sv20240301@student.edu.vn', 'BUS101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240302@student.edu.vn', 'BUS101-01-2026A', 'ACTIVE', FALSE),

    -- Past Semester Enrollments (HK2 2025-2026)
    ('sv20240101@student.edu.vn', 'GEN101-02-2025B', 'COMPLETED', FALSE),
    ('sv20240102@student.edu.vn', 'GEN101-02-2025B', 'COMPLETED', FALSE),
    ('sv20240201@student.edu.vn', 'IT101-02-2025B', 'COMPLETED', FALSE),
    ('sv20240202@student.edu.vn', 'IT101-02-2025B', 'FAILED', FALSE)
) AS v(email, class_code, status, is_retake)
JOIN users u ON u.email = v.email
JOIN classes c ON c.class_code = v.class_code
ON CONFLICT (student_id, class_id) DO NOTHING;

-- ──────────────────────────────────────────────
-- 5. ADD GRADES FOR PAST SEMESTER (and partial for current)
-- ──────────────────────────────────────────────
INSERT INTO grades (class_id, student_id, midterm_score, final_score, total_score)
SELECT c.id, u.id, v.midterm, v.final, v.total
FROM (VALUES
    -- Passed GEN101
    ('GEN101-02-2025B', 'sv20240101@student.edu.vn', 8.0, 8.5, 8.25),
    ('GEN101-02-2025B', 'sv20240102@student.edu.vn', 9.0, 7.5, 8.25),
    
    -- IT101 (Past)
    ('IT101-02-2025B', 'sv20240201@student.edu.vn', 7.5, 8.0, 7.80),
    ('IT101-02-2025B', 'sv20240202@student.edu.vn', 4.0, 3.5, 3.70), -- FAILED
    
    -- Current Semester (Midterm only)
    ('IT101-01-2026A', 'sv20240101@student.edu.vn', 8.5, NULL, NULL),
    ('IT101-01-2026A', 'sv20240102@student.edu.vn', 9.0, NULL, NULL),
    ('IT101-01-2026A', 'sv20240103@student.edu.vn', 6.5, NULL, NULL),
    ('IT101-01-2026A', 'sv20240104@student.edu.vn', 5.0, NULL, NULL),
    ('IT101-01-2026A', 'sv20240105@student.edu.vn', 7.0, NULL, NULL)
) AS v(class_code, email, midterm, final, total)
JOIN users u ON u.email = v.email
JOIN classes c ON c.class_code = v.class_code
ON CONFLICT (class_id, student_id) DO NOTHING;
