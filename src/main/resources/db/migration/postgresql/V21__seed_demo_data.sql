-- Demo seed data for FE/manual testing on PostgreSQL.
-- All demo accounts use the same BCrypt password: password

INSERT INTO administrative_classes (class_name, faculty, academic_year)
VALUES
    ('CNTT-K65A', 'Công nghệ thông tin', '2024-2028'),
    ('CNTT-K65B', 'Công nghệ thông tin', '2024-2028'),
    ('QTKD-K65A', 'Quản trị kinh doanh', '2024-2028')
ON CONFLICT (class_name) DO NOTHING;

INSERT INTO users (
    email, password, full_name, date_of_birth, role, student_code, lecturer_code,
    faculty, major, is_first_login, status, admin_class_id
)
VALUES
    ('admin@learninghub.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Quản trị hệ thống', DATE '1990-01-01', 'ADMIN', NULL, NULL, 'Phòng đào tạo', NULL, FALSE, 'ACTIVE', NULL),
    ('gv.nguyenvana@learninghub.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Nguyễn Văn An', DATE '1984-03-12', 'LECTURER', NULL, 'GV001', 'Công nghệ thông tin', 'Kỹ thuật phần mềm', FALSE, 'ACTIVE', NULL),
    ('gv.tranthib@learninghub.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Trần Thị Bình', DATE '1988-09-20', 'LECTURER', NULL, 'GV002', 'Công nghệ thông tin', 'Hệ thống thông tin', FALSE, 'ACTIVE', NULL),
    ('gv.leminhc@learninghub.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Lê Minh Cường', DATE '1982-11-05', 'LECTURER', NULL, 'GV003', 'Quản trị kinh doanh', 'Marketing', FALSE, 'ACTIVE', NULL),
    ('sv20240001@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Phạm Hoàng Nam', DATE '2006-02-14', 'STUDENT', 'SV20240001', NULL, 'Công nghệ thông tin', 'Kỹ thuật phần mềm', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240002@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Đỗ Minh Anh', DATE '2006-06-22', 'STUDENT', 'SV20240002', NULL, 'Công nghệ thông tin', 'Kỹ thuật phần mềm', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240003@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Nguyễn Thùy Linh', DATE '2006-10-09', 'STUDENT', 'SV20240003', NULL, 'Công nghệ thông tin', 'Hệ thống thông tin', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240004@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Trần Quốc Huy', DATE '2005-12-01', 'STUDENT', 'SV20240004', NULL, 'Công nghệ thông tin', 'Hệ thống thông tin', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240005@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Vũ Ngọc Mai', DATE '2006-04-18', 'STUDENT', 'SV20240005', NULL, 'Quản trị kinh doanh', 'Marketing', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'QTKD-K65A')),
    ('sv20240006@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Hoàng Gia Bảo', DATE '2006-08-30', 'STUDENT', 'SV20240006', NULL, 'Quản trị kinh doanh', 'Marketing', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'QTKD-K65A')),
    ('sv20240007@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Bùi Khánh Vy', DATE '2006-01-25', 'STUDENT', 'SV20240007', NULL, 'Công nghệ thông tin', 'Kỹ thuật phần mềm', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240008@student.edu.vn', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa', 'Lê Đức Phúc', DATE '2005-07-11', 'STUDENT', 'SV20240008', NULL, 'Công nghệ thông tin', 'Hệ thống thông tin', TRUE, 'INACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B'))
ON CONFLICT DO NOTHING;

INSERT INTO courses (code, title, description, credit)
VALUES
    ('IT101', 'Nhập môn lập trình', 'Kiến thức nền tảng về thuật toán, biến, điều kiện, vòng lặp và hàm.', 3),
    ('IT202', 'Cơ sở dữ liệu', 'Thiết kế mô hình dữ liệu, SQL, chuẩn hóa và giao dịch.', 3),
    ('IT303', 'Công nghệ phần mềm', 'Quy trình phát triển phần mềm, phân tích yêu cầu, kiểm thử và triển khai.', 3),
    ('BUS101', 'Nguyên lý Marketing', 'Nghiên cứu thị trường, hành vi khách hàng và chiến lược marketing căn bản.', 2),
    ('GEN101', 'Kỹ năng học đại học', 'Kỹ năng tự học, làm việc nhóm và thuyết trình trong môi trường đại học.', 2)
ON CONFLICT (code) DO NOTHING;

INSERT INTO classes (class_code, class_name, semester, academic_year, course_id, lecturer_id, max_students)
VALUES
    ('IT101-01-2026A', 'Nhập môn lập trình - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'IT101'), (SELECT id FROM users WHERE lecturer_code = 'GV001'), 45),
    ('IT202-01-2026A', 'Cơ sở dữ liệu - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'IT202'), (SELECT id FROM users WHERE lecturer_code = 'GV002'), 40),
    ('IT303-01-2026A', 'Công nghệ phần mềm - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'IT303'), (SELECT id FROM users WHERE lecturer_code = 'GV001'), 35),
    ('BUS101-01-2026A', 'Nguyên lý Marketing - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'BUS101'), (SELECT id FROM users WHERE lecturer_code = 'GV003'), 50),
    ('GEN101-01-2026A', 'Kỹ năng học đại học - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'GEN101'), NULL, 60)
ON CONFLICT (class_code) DO NOTHING;

INSERT INTO enrollments (student_id, class_id, status, is_retake)
SELECT u.id, c.id, v.status, v.is_retake
FROM (VALUES
    ('sv20240001@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240002@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240003@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240004@student.edu.vn', 'IT101-01-2026A', 'ACTIVE', TRUE),
    ('sv20240001@student.edu.vn', 'IT202-01-2026A', 'ACTIVE', FALSE),
    ('sv20240002@student.edu.vn', 'IT202-01-2026A', 'ACTIVE', FALSE),
    ('sv20240003@student.edu.vn', 'IT202-01-2026A', 'ACTIVE', FALSE),
    ('sv20240005@student.edu.vn', 'BUS101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240006@student.edu.vn', 'BUS101-01-2026A', 'ACTIVE', FALSE),
    ('sv20240007@student.edu.vn', 'IT303-01-2026A', 'ACTIVE', FALSE),
    ('sv20240001@student.edu.vn', 'GEN101-01-2026A', 'COMPLETED', FALSE)
) AS v(email, class_code, status, is_retake)
JOIN users u ON u.email = v.email
JOIN classes c ON c.class_code = v.class_code
ON CONFLICT (student_id, class_id) DO NOTHING;

INSERT INTO chapters (clazz_id, title, description, sort_order)
SELECT c.id, v.title, v.description, v.sort_order
FROM (VALUES
    ('IT101-01-2026A', 'Chương 1: Làm quen lập trình', 'Cài đặt môi trường và viết chương trình đầu tiên.', 1),
    ('IT101-01-2026A', 'Chương 2: Cấu trúc điều khiển', 'Điều kiện, vòng lặp và xử lý lỗi cơ bản.', 2),
    ('IT202-01-2026A', 'Chương 1: Mô hình quan hệ', 'Bảng, khóa chính, khóa ngoại và ràng buộc dữ liệu.', 1),
    ('IT202-01-2026A', 'Chương 2: Truy vấn SQL', 'SELECT, JOIN, GROUP BY và truy vấn con.', 2),
    ('IT303-01-2026A', 'Chương 1: Quy trình phần mềm', 'Agile, Scrum và vòng đời phát triển phần mềm.', 1),
    ('BUS101-01-2026A', 'Chương 1: Tổng quan Marketing', 'Khái niệm thị trường và chân dung khách hàng.', 1)
) AS v(class_code, title, description, sort_order)
JOIN classes c ON c.class_code = v.class_code
WHERE NOT EXISTS (
    SELECT 1 FROM chapters ch WHERE ch.clazz_id = c.id AND ch.title = v.title
);

INSERT INTO lessons (chapter_id, title, content, video_url, duration, sort_order)
SELECT ch.id, v.title, v.content, v.video_url, v.duration, v.sort_order
FROM (VALUES
    ('IT101-01-2026A', 'Chương 1: Làm quen lập trình', 'Bài 1: Cài đặt môi trường', 'Hướng dẫn cài JDK/IDE và chạy chương trình đầu tiên.', 'https://res.cloudinary.com/demo/video/upload/sample.mp4', 18, 1),
    ('IT101-01-2026A', 'Chương 1: Làm quen lập trình', 'Bài 2: Biến và kiểu dữ liệu', 'Biến, kiểu số, chuỗi và boolean.', NULL, 25, 2),
    ('IT101-01-2026A', 'Chương 2: Cấu trúc điều khiển', 'Bài 3: If/else và switch', 'Cách rẽ nhánh chương trình theo điều kiện.', NULL, 30, 1),
    ('IT202-01-2026A', 'Chương 1: Mô hình quan hệ', 'Bài 1: Thiết kế ERD', 'Xác định thực thể, thuộc tính và quan hệ.', NULL, 35, 1),
    ('IT202-01-2026A', 'Chương 2: Truy vấn SQL', 'Bài 2: Join nhiều bảng', 'INNER JOIN, LEFT JOIN và tình huống sử dụng.', NULL, 40, 1),
    ('IT303-01-2026A', 'Chương 1: Quy trình phần mềm', 'Bài 1: User story', 'Viết user story và acceptance criteria.', NULL, 28, 1),
    ('BUS101-01-2026A', 'Chương 1: Tổng quan Marketing', 'Bài 1: Phân khúc thị trường', 'Segment, target và positioning.', NULL, 32, 1)
) AS v(class_code, chapter_title, title, content, video_url, duration, sort_order)
JOIN classes c ON c.class_code = v.class_code
JOIN chapters ch ON ch.clazz_id = c.id AND ch.title = v.chapter_title
WHERE NOT EXISTS (
    SELECT 1 FROM lessons l WHERE l.chapter_id = ch.id AND l.title = v.title
);

INSERT INTO assignments (class_id, title, description, due_date, max_score)
SELECT c.id, v.title, v.description, v.due_date, v.max_score
FROM (VALUES
    ('IT101-01-2026A', 'Bài tập 1: Máy tính BMI', 'Viết chương trình nhập chiều cao/cân nặng và tính BMI.', CURRENT_TIMESTAMP + INTERVAL '7 days', 10.00),
    ('IT202-01-2026A', 'Bài tập 1: Thiết kế CSDL LMS', 'Vẽ ERD và giải thích các ràng buộc chính.', CURRENT_TIMESTAMP + INTERVAL '10 days', 10.00),
    ('IT303-01-2026A', 'Bài tập nhóm: Đặc tả yêu cầu', 'Phân tích yêu cầu cho một module trong hệ thống giáo dục.', CURRENT_TIMESTAMP + INTERVAL '14 days', 10.00),
    ('BUS101-01-2026A', 'Case study: Chiến dịch truyền thông', 'Phân tích một chiến dịch marketing giáo dục.', CURRENT_TIMESTAMP + INTERVAL '9 days', 10.00)
) AS v(class_code, title, description, due_date, max_score)
JOIN classes c ON c.class_code = v.class_code
WHERE NOT EXISTS (
    SELECT 1 FROM assignments a WHERE a.class_id = c.id AND a.title = v.title
);

INSERT INTO submissions (assignment_id, student_id, file_url, submitted_at, score, is_late, feedback)
SELECT a.id, u.id, v.file_url, v.submitted_at, v.score, v.is_late, v.feedback
FROM (VALUES
    ('IT101-01-2026A', 'Bài tập 1: Máy tính BMI', 'sv20240001@student.edu.vn', 'https://example.com/submissions/sv20240001-bmi.pdf', CURRENT_TIMESTAMP - INTERVAL '1 day', 8.50, FALSE, 'Bài làm đúng yêu cầu, cần trình bày rõ hơn.'),
    ('IT101-01-2026A', 'Bài tập 1: Máy tính BMI', 'sv20240002@student.edu.vn', 'https://example.com/submissions/sv20240002-bmi.pdf', CURRENT_TIMESTAMP - INTERVAL '2 hours', 9.00, FALSE, 'Code sạch, xử lý input tốt.'),
    ('IT202-01-2026A', 'Bài tập 1: Thiết kế CSDL LMS', 'sv20240001@student.edu.vn', 'https://example.com/submissions/sv20240001-erd.pdf', CURRENT_TIMESTAMP - INTERVAL '3 hours', 8.00, FALSE, 'ERD ổn, bổ sung cardinality.'),
    ('BUS101-01-2026A', 'Case study: Chiến dịch truyền thông', 'sv20240005@student.edu.vn', 'https://example.com/submissions/sv20240005-marketing.pdf', CURRENT_TIMESTAMP + INTERVAL '1 day', NULL, FALSE, NULL)
) AS v(class_code, assignment_title, email, file_url, submitted_at, score, is_late, feedback)
JOIN classes c ON c.class_code = v.class_code
JOIN assignments a ON a.class_id = c.id AND a.title = v.assignment_title
JOIN users u ON u.email = v.email
ON CONFLICT (assignment_id, student_id) DO NOTHING;

INSERT INTO quizzes (class_id, title, duration_minutes, total_score)
SELECT c.id, v.title, v.duration_minutes, v.total_score
FROM (VALUES
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 20, 10.00),
    ('IT202-01-2026A', 'Quiz 1: Khóa và quan hệ', 25, 10.00),
    ('BUS101-01-2026A', 'Quiz 1: Marketing căn bản', 15, 10.00)
) AS v(class_code, title, duration_minutes, total_score)
JOIN classes c ON c.class_code = v.class_code
WHERE NOT EXISTS (
    SELECT 1 FROM quizzes q WHERE q.class_id = c.id AND q.title = v.title
);

INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer)
SELECT q.id, v.question_text, v.option_a, v.option_b, v.option_c, v.option_d, v.correct_answer
FROM (VALUES
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 'Kiểu dữ liệu nào dùng để lưu đúng/sai?', 'String', 'Boolean', 'Integer', 'Double', 'B'),
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 'Cấu trúc nào dùng để rẽ nhánh chương trình?', 'for', 'while', 'if/else', 'class', 'C'),
    ('IT202-01-2026A', 'Quiz 1: Khóa và quan hệ', 'Khóa chính dùng để làm gì?', 'Mã hóa dữ liệu', 'Định danh duy nhất một dòng', 'Sắp xếp bảng', 'Xóa bảng', 'B'),
    ('IT202-01-2026A', 'Quiz 1: Khóa và quan hệ', 'Foreign key biểu diễn điều gì?', 'Quan hệ giữa bảng', 'Tên database', 'Dung lượng bảng', 'Mật khẩu', 'A'),
    ('BUS101-01-2026A', 'Quiz 1: Marketing căn bản', 'STP gồm những bước nào?', 'Sell-Test-Plan', 'Segment-Target-Position', 'Search-Track-Publish', 'Study-Train-Practice', 'B')
) AS v(class_code, quiz_title, question_text, option_a, option_b, option_c, option_d, correct_answer)
JOIN classes c ON c.class_code = v.class_code
JOIN quizzes q ON q.class_id = c.id AND q.title = v.quiz_title
WHERE NOT EXISTS (
    SELECT 1 FROM questions qu WHERE qu.quiz_id = q.id AND qu.question_text = v.question_text
);

INSERT INTO quiz_attempts (quiz_id, student_id, score, started_at, submitted_at)
SELECT q.id, u.id, v.score, v.started_at, v.submitted_at
FROM (VALUES
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 'sv20240001@student.edu.vn', 8.00, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '18 minutes'),
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 'sv20240002@student.edu.vn', 9.00, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '16 minutes'),
    ('IT202-01-2026A', 'Quiz 1: Khóa và quan hệ', 'sv20240001@student.edu.vn', 7.50, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '22 minutes'),
    ('BUS101-01-2026A', 'Quiz 1: Marketing căn bản', 'sv20240005@student.edu.vn', 8.50, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '12 minutes')
) AS v(class_code, quiz_title, email, score, started_at, submitted_at)
JOIN classes c ON c.class_code = v.class_code
JOIN quizzes q ON q.class_id = c.id AND q.title = v.quiz_title
JOIN users u ON u.email = v.email
WHERE NOT EXISTS (
    SELECT 1 FROM quiz_attempts qa WHERE qa.quiz_id = q.id AND qa.student_id = u.id
);

INSERT INTO grades (class_id, student_id, midterm_score, final_score, total_score)
SELECT c.id, u.id, v.midterm_score, v.final_score, v.total_score
FROM (VALUES
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', 8.00, 8.50, 8.30),
    ('IT101-01-2026A', 'sv20240002@student.edu.vn', 9.00, 8.80, 8.90),
    ('IT101-01-2026A', 'sv20240003@student.edu.vn', 6.50, 7.00, 6.80),
    ('IT101-01-2026A', 'sv20240004@student.edu.vn', 4.00, 4.50, 4.30),
    ('IT202-01-2026A', 'sv20240001@student.edu.vn', 7.50, 8.00, 7.80),
    ('IT202-01-2026A', 'sv20240002@student.edu.vn', 8.00, NULL, NULL),
    ('BUS101-01-2026A', 'sv20240005@student.edu.vn', 8.50, 9.00, 8.80),
    ('GEN101-01-2026A', 'sv20240001@student.edu.vn', 8.00, 8.00, 8.00)
) AS v(class_code, email, midterm_score, final_score, total_score)
JOIN classes c ON c.class_code = v.class_code
JOIN users u ON u.email = v.email
ON CONFLICT (class_id, student_id) DO NOTHING;

INSERT INTO attendance (class_id, student_id, attendance_date, status)
SELECT c.id, u.id, v.attendance_date, v.status
FROM (VALUES
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', CURRENT_DATE - 21, 'PRESENT'),
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', CURRENT_DATE - 14, 'PRESENT'),
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', CURRENT_DATE - 7, 'LATE'),
    ('IT101-01-2026A', 'sv20240002@student.edu.vn', CURRENT_DATE - 21, 'PRESENT'),
    ('IT101-01-2026A', 'sv20240002@student.edu.vn', CURRENT_DATE - 14, 'PRESENT'),
    ('IT101-01-2026A', 'sv20240002@student.edu.vn', CURRENT_DATE - 7, 'PRESENT'),
    ('IT101-01-2026A', 'sv20240004@student.edu.vn', CURRENT_DATE - 21, 'ABSENT'),
    ('IT101-01-2026A', 'sv20240004@student.edu.vn', CURRENT_DATE - 14, 'ABSENT'),
    ('IT202-01-2026A', 'sv20240001@student.edu.vn', CURRENT_DATE - 10, 'PRESENT'),
    ('IT202-01-2026A', 'sv20240002@student.edu.vn', CURRENT_DATE - 10, 'LATE'),
    ('BUS101-01-2026A', 'sv20240005@student.edu.vn', CURRENT_DATE - 6, 'PRESENT')
) AS v(class_code, email, attendance_date, status)
JOIN classes c ON c.class_code = v.class_code
JOIN users u ON u.email = v.email
ON CONFLICT (class_id, student_id, attendance_date) DO NOTHING;

INSERT INTO announcements (class_id, title, content)
SELECT c.id, v.title, v.content
FROM (VALUES
    ('IT101-01-2026A', 'Lịch học tuần này', 'Lớp học tại phòng A301, sinh viên chuẩn bị laptop cá nhân.'),
    ('IT202-01-2026A', 'Nộp bài ERD', 'Deadline bài ERD là cuối tuần, nộp đúng định dạng PDF.'),
    ('BUS101-01-2026A', 'Thảo luận case study', 'Buổi sau mỗi nhóm trình bày 5 phút về chiến dịch đã chọn.')
) AS v(class_code, title, content)
JOIN classes c ON c.class_code = v.class_code
WHERE NOT EXISTS (
    SELECT 1 FROM announcements a WHERE a.class_id = c.id AND a.title = v.title
);

INSERT INTO password_reset_tokens (user_id, token, expires_at, used)
SELECT u.id, v.token, CURRENT_TIMESTAMP + INTERVAL '2 hours', FALSE
FROM (VALUES
    ('sv20240008@student.edu.vn', 'demo-reset-token-sv20240008')
) AS v(email, token)
JOIN users u ON u.email = v.email
WHERE NOT EXISTS (
    SELECT 1 FROM password_reset_tokens prt WHERE prt.token = v.token
);

INSERT INTO refresh_tokens (user_id, token, expires_at, revoked)
SELECT u.id, v.token, CURRENT_TIMESTAMP + INTERVAL '7 days', FALSE
FROM (VALUES
    ('admin@learninghub.edu.vn', 'demo-refresh-token-admin'),
    ('sv20240001@student.edu.vn', 'demo-refresh-token-student')
) AS v(email, token)
JOIN users u ON u.email = v.email
ON CONFLICT (token) DO NOTHING;

INSERT INTO lesson_progress (enrollment_id, lesson_id, is_completed, completed_at)
SELECT e.id, l.id, v.is_completed, v.completed_at
FROM (VALUES
    ('sv20240001@student.edu.vn', 'IT101-01-2026A', 'Bài 1: Cài đặt môi trường', TRUE, CURRENT_TIMESTAMP - INTERVAL '5 days'),
    ('sv20240001@student.edu.vn', 'IT101-01-2026A', 'Bài 2: Biến và kiểu dữ liệu', TRUE, CURRENT_TIMESTAMP - INTERVAL '4 days'),
    ('sv20240002@student.edu.vn', 'IT101-01-2026A', 'Bài 1: Cài đặt môi trường', TRUE, CURRENT_TIMESTAMP - INTERVAL '4 days'),
    ('sv20240002@student.edu.vn', 'IT101-01-2026A', 'Bài 2: Biến và kiểu dữ liệu', FALSE, NULL),
    ('sv20240001@student.edu.vn', 'IT202-01-2026A', 'Bài 1: Thiết kế ERD', TRUE, CURRENT_TIMESTAMP - INTERVAL '2 days')
) AS v(email, class_code, lesson_title, is_completed, completed_at)
JOIN users u ON u.email = v.email
JOIN classes c ON c.class_code = v.class_code
JOIN enrollments e ON e.student_id = u.id AND e.class_id = c.id
JOIN chapters ch ON ch.clazz_id = c.id
JOIN lessons l ON l.chapter_id = ch.id AND l.title = v.lesson_title
ON CONFLICT (enrollment_id, lesson_id) DO NOTHING;

INSERT INTO notifications (recipient_id, type, title, content, reference_id, is_read)
SELECT u.id, v.type, v.title, v.content, v.reference_id::BIGINT, v.is_read
FROM (VALUES
    ('sv20240001@student.edu.vn', 'NEW_ASSIGNMENT', 'Có bài tập mới', 'Bài tập BMI đã được giao cho lớp Nhập môn lập trình.', NULL, FALSE),
    ('sv20240001@student.edu.vn', 'NEW_GRADE', 'Đã có điểm mới', 'Điểm lớp Cơ sở dữ liệu đã được cập nhật.', NULL, TRUE),
    ('sv20240002@student.edu.vn', 'NEW_ANNOUNCEMENT', 'Thông báo lớp học', 'Lịch học tuần này đã được cập nhật.', NULL, FALSE),
    ('gv.nguyenvana@learninghub.edu.vn', 'NEW_ANNOUNCEMENT', 'Nhắc lịch nhập điểm', 'Vui lòng hoàn tất nhập điểm giữa kỳ trong tuần này.', NULL, FALSE)
) AS v(email, type, title, content, reference_id, is_read)
JOIN users u ON u.email = v.email
WHERE NOT EXISTS (
    SELECT 1 FROM notifications n WHERE n.recipient_id = u.id AND n.title = v.title AND n.content = v.content
);

INSERT INTO forum_posts (clazz_id, author_id, title, content)
SELECT c.id, u.id, v.title, v.content
FROM (VALUES
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', 'Hỏi về bài tập BMI', 'Cho em hỏi có cần validate cân nặng âm không ạ?'),
    ('IT202-01-2026A', 'gv.tranthib@learninghub.edu.vn', 'Tài liệu ôn SQL JOIN', 'Các bạn đọc thêm tài liệu về INNER JOIN và LEFT JOIN trước buổi sau.')
) AS v(class_code, email, title, content)
JOIN classes c ON c.class_code = v.class_code
JOIN users u ON u.email = v.email
WHERE NOT EXISTS (
    SELECT 1 FROM forum_posts fp WHERE fp.clazz_id = c.id AND fp.title = v.title
);

INSERT INTO forum_comments (post_id, author_id, content)
SELECT fp.id, u.id, v.content
FROM (VALUES
    ('IT101-01-2026A', 'Hỏi về bài tập BMI', 'gv.nguyenvana@learninghub.edu.vn', 'Có nhé, em xử lý input không hợp lệ và báo lỗi thân thiện.'),
    ('IT202-01-2026A', 'Tài liệu ôn SQL JOIN', 'sv20240001@student.edu.vn', 'Em đã đọc, phần LEFT JOIN khá hữu ích ạ.')
) AS v(class_code, post_title, email, content)
JOIN classes c ON c.class_code = v.class_code
JOIN forum_posts fp ON fp.clazz_id = c.id AND fp.title = v.post_title
JOIN users u ON u.email = v.email
WHERE NOT EXISTS (
    SELECT 1 FROM forum_comments fc WHERE fc.post_id = fp.id AND fc.author_id = u.id AND fc.content = v.content
);

INSERT INTO class_schedules (clazz_id, day_of_week, start_period, end_period, room)
SELECT c.id, v.day_of_week, v.start_period, v.end_period, v.room
FROM (VALUES
    ('IT101-01-2026A', 2, 1, 3, 'A301'),
    ('IT101-01-2026A', 4, 4, 6, 'LAB-02'),
    ('IT202-01-2026A', 3, 1, 3, 'B204'),
    ('IT303-01-2026A', 5, 7, 9, 'LAB-01'),
    ('BUS101-01-2026A', 6, 1, 2, 'C105'),
    ('GEN101-01-2026A', 7, 1, 2, 'Online')
) AS v(class_code, day_of_week, start_period, end_period, room)
JOIN classes c ON c.class_code = v.class_code
WHERE NOT EXISTS (
    SELECT 1 FROM class_schedules cs
    WHERE cs.clazz_id = c.id
      AND cs.day_of_week = v.day_of_week
      AND cs.start_period = v.start_period
      AND cs.end_period = v.end_period
);

INSERT INTO registration_periods (name, semester, academic_year, open_at, close_at, max_credits, is_active)
SELECT v.name, v.semester, v.academic_year, v.open_at, v.close_at, v.max_credits,
       CASE WHEN NOT EXISTS (SELECT 1 FROM registration_periods rp WHERE rp.is_active) THEN v.is_active ELSE FALSE END
FROM (VALUES
    ('Đăng ký học phần HK1 2026-2027', 'HK1', '2026-2027', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '21 days', 24, TRUE),
    ('Đăng ký học phần HK2 2025-2026', 'HK2', '2025-2026', CURRENT_TIMESTAMP - INTERVAL '120 days', CURRENT_TIMESTAMP - INTERVAL '90 days', 22, FALSE)
) AS v(name, semester, academic_year, open_at, close_at, max_credits, is_active)
WHERE NOT EXISTS (
    SELECT 1 FROM registration_periods rp WHERE rp.name = v.name
);

INSERT INTO curricula (name, faculty, academic_year, is_active)
SELECT v.name, v.faculty, v.academic_year, v.is_active
FROM (VALUES
    ('Chương trình Kỹ thuật phần mềm K65', 'Công nghệ thông tin', '2024-2028', TRUE),
    ('Chương trình Marketing K65', 'Quản trị kinh doanh', '2024-2028', TRUE)
) AS v(name, faculty, academic_year, is_active)
WHERE NOT EXISTS (
    SELECT 1 FROM curricula cu WHERE cu.name = v.name AND cu.academic_year = v.academic_year
);

INSERT INTO curriculum_courses (curriculum_id, course_id, semester_no, is_required)
SELECT cu.id, co.id, v.semester_no, v.is_required
FROM (VALUES
    ('Chương trình Kỹ thuật phần mềm K65', 'IT101', 1, TRUE),
    ('Chương trình Kỹ thuật phần mềm K65', 'IT202', 2, TRUE),
    ('Chương trình Kỹ thuật phần mềm K65', 'IT303', 4, TRUE),
    ('Chương trình Kỹ thuật phần mềm K65', 'GEN101', 1, TRUE),
    ('Chương trình Marketing K65', 'BUS101', 1, TRUE),
    ('Chương trình Marketing K65', 'GEN101', 1, TRUE)
) AS v(curriculum_name, course_code, semester_no, is_required)
JOIN curricula cu ON cu.name = v.curriculum_name
JOIN courses co ON co.code = v.course_code
ON CONFLICT (curriculum_id, course_id) DO NOTHING;

INSERT INTO course_prerequisites (course_id, prerequisite_course_id)
SELECT co.id, pre.id
FROM (VALUES
    ('IT202', 'IT101'),
    ('IT303', 'IT202')
) AS v(course_code, prerequisite_code)
JOIN courses co ON co.code = v.course_code
JOIN courses pre ON pre.code = v.prerequisite_code
ON CONFLICT (course_id, prerequisite_course_id) DO NOTHING;

INSERT INTO departments (code, name, description, head_user_id, is_active)
VALUES
    ('FIT', 'Khoa Công nghệ thông tin', 'Quản lý các ngành Công nghệ thông tin, Kỹ thuật phần mềm và Hệ thống thông tin.', (SELECT id FROM users WHERE lecturer_code = 'GV001'), TRUE),
    ('FBA', 'Khoa Quản trị kinh doanh', 'Quản lý các ngành kinh doanh, marketing và quản trị.', (SELECT id FROM users WHERE lecturer_code = 'GV003'), TRUE)
ON CONFLICT (code) DO NOTHING;

INSERT INTO tuition_rates (academic_year, price_per_credit, is_active)
VALUES
    ('2025-2026', 450000.00, FALSE),
    ('2026-2027', 500000.00, TRUE)
ON CONFLICT (academic_year) DO NOTHING;

INSERT INTO tuition_invoices (student_id, semester, academic_year, total_credits, price_per_credit, amount, status, paid_at)
SELECT u.id, v.semester, v.academic_year, v.total_credits, v.price_per_credit, v.amount, v.status, v.paid_at
FROM (VALUES
    ('sv20240001@student.edu.vn', 'HK1', '2026-2027', 11, 500000.00, 5500000.00, 'UNPAID', NULL),
    ('sv20240002@student.edu.vn', 'HK1', '2026-2027', 6, 500000.00, 3000000.00, 'PAID', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('sv20240005@student.edu.vn', 'HK1', '2026-2027', 2, 500000.00, 1000000.00, 'UNPAID', NULL)
) AS v(email, semester, academic_year, total_credits, price_per_credit, amount, status, paid_at)
JOIN users u ON u.email = v.email
ON CONFLICT (student_id, semester, academic_year) DO NOTHING;
