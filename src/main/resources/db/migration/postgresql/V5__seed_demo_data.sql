-- Demo data for manual FE testing.
-- This is an additive migration and intentionally does NOT replace existing seed scripts.
-- It focuses on the data most useful for login, dashboard, class detail, notifications, forum, and registration flows.

INSERT INTO administrative_classes (class_name, faculty, academic_year)
VALUES
    ('CNTT-K65A', 'Công nghệ thông tin', '2024-2028'),
    ('CNTT-K65B', 'Công nghệ thông tin', '2024-2028'),
    ('QTKD-K65A', 'Quản trị kinh doanh', '2024-2028')
ON CONFLICT (class_name) DO NOTHING;

INSERT INTO users (
    email, password, full_name, avatar_url, date_of_birth, role, student_code,
    lecturer_code, faculty, major, is_first_login, status, admin_class_id
)
VALUES
    ('admin@learninghub.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Quản trị hệ thống', NULL, DATE '1990-01-01', 'ADMIN', NULL, NULL, 'Phòng đào tạo', NULL, FALSE, 'ACTIVE', NULL),
    ('gv.nguyenvana@learninghub.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Nguyễn Văn An', NULL, DATE '1984-03-12', 'LECTURER', NULL, 'GV001', 'Công nghệ thông tin', 'Kỹ thuật phần mềm', FALSE, 'ACTIVE', NULL),
    ('gv.tranthib@learninghub.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Trần Thị Bình', NULL, DATE '1988-09-20', 'LECTURER', NULL, 'GV002', 'Công nghệ thông tin', 'Hệ thống thông tin', FALSE, 'ACTIVE', NULL),
    ('gv.leminhc@learninghub.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Lê Minh Cường', NULL, DATE '1982-11-05', 'LECTURER', NULL, 'GV003', 'Quản trị kinh doanh', 'Marketing', FALSE, 'ACTIVE', NULL),
    ('sv20240001@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Phạm Hoàng Nam', NULL, DATE '2006-02-14', 'STUDENT', 'SV20240001', NULL, 'Công nghệ thông tin', 'Kỹ thuật phần mềm', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240002@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Đỗ Minh Anh', NULL, DATE '2006-06-22', 'STUDENT', 'SV20240002', NULL, 'Công nghệ thông tin', 'Kỹ thuật phần mềm', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65A')),
    ('sv20240003@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Nguyễn Thùy Linh', NULL, DATE '2006-10-09', 'STUDENT', 'SV20240003', NULL, 'Công nghệ thông tin', 'Hệ thống thông tin', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240004@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Trần Quốc Huy', NULL, DATE '2005-12-01', 'STUDENT', 'SV20240004', NULL, 'Công nghệ thông tin', 'Hệ thống thông tin', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'CNTT-K65B')),
    ('sv20240005@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Vũ Ngọc Mai', NULL, DATE '2006-04-18', 'STUDENT', 'SV20240005', NULL, 'Quản trị kinh doanh', 'Marketing', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'QTKD-K65A')),
    ('sv20240006@student.edu.vn', '$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm', 'Hoàng Gia Bảo', NULL, DATE '2006-08-30', 'STUDENT', 'SV20240006', NULL, 'Quản trị kinh doanh', 'Marketing', TRUE, 'ACTIVE', (SELECT id FROM administrative_classes WHERE class_name = 'QTKD-K65A'))
ON CONFLICT (email) DO NOTHING;

INSERT INTO courses (title, description, code, credit)
VALUES
    ('Nhập môn lập trình', 'Kiến thức nền tảng về lập trình, biến, điều kiện, vòng lặp.', 'IT101', 3),
    ('Cơ sở dữ liệu', 'Mô hình quan hệ, SQL và thiết kế dữ liệu.', 'IT202', 3),
    ('Công nghệ phần mềm', 'Quy trình phát triển phần mềm, kiểm thử và triển khai.', 'IT303', 3),
    ('Nguyên lý Marketing', 'Khái niệm của marketing và khách hàng.', 'BUS101', 2),
    ('Kỹ năng học đại học', 'Kỹ năng tự học, thuyết trình và làm việc nhóm.', 'GEN101', 2)
ON CONFLICT (code) DO NOTHING;

INSERT INTO classes (class_code, class_name, semester, academic_year, course_id, lecturer_id, max_students)
VALUES
    ('IT101-01-2026A', 'Nhập môn lập trình - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'IT101'), (SELECT id FROM users WHERE lecturer_code = 'GV001'), 45),
    ('IT202-01-2026A', 'Cơ sở dữ liệu - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'IT202'), (SELECT id FROM users WHERE lecturer_code = 'GV002'), 40),
    ('IT303-01-2026A', 'Công nghệ phần mềm - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'IT303'), (SELECT id FROM users WHERE lecturer_code = 'GV001'), 35),
    ('BUS101-01-2026A', 'Nguyên lý Marketing - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'BUS101'), (SELECT id FROM users WHERE lecturer_code = 'GV003'), 50),
    ('GEN101-01-2026A', 'Kỹ năng học đại học - Nhóm 01', 'HK1', '2026-2027', (SELECT id FROM courses WHERE code = 'GEN101'), NULL, 60)
ON CONFLICT (class_code) DO NOTHING;

INSERT INTO enrollments (student_id, class_id, enrolled_at, status, is_retake)
SELECT u.id, c.id, CURRENT_TIMESTAMP, 'ACTIVE', FALSE
FROM users u
JOIN classes c ON c.class_code IN ('IT101-01-2026A', 'IT202-01-2026A', 'BUS101-01-2026A')
WHERE u.email IN ('sv20240001@student.edu.vn', 'sv20240002@student.edu.vn', 'sv20240003@student.edu.vn', 'sv20240005@student.edu.vn', 'sv20240006@student.edu.vn')
ON CONFLICT (student_id, class_id) DO NOTHING;

INSERT INTO chapters (clazz_id, title, description, sort_order)
SELECT c.id, v.title, v.description, v.sort_order
FROM (VALUES
    ('IT101-01-2026A', 'Chương 1: Làm quen lập trình', 'Cài đặt môi trường và viết chương trình đầu tiên.', 1),
    ('IT101-01-2026A', 'Chương 2: Cấu trúc điều khiển', 'Điều kiện, vòng lặp và xử lý lỗi cơ bản.', 2),
    ('IT202-01-2026A', 'Chương 1: Mô hình quan hệ', 'Bảng, khóa chính và khóa ngoại.', 1),
    ('IT202-01-2026A', 'Chương 2: Truy vấn SQL', 'SELECT, JOIN, GROUP BY và truy vấn con.', 2),
    ('BUS101-01-2026A', 'Chương 1: Tổng quan Marketing', 'Khái niệm thị trường và khách hàng.', 1)
) AS v(class_code, title, description, sort_order)
JOIN classes c ON c.class_code = v.class_code
ON CONFLICT DO NOTHING;

INSERT INTO lessons (chapter_id, title, content, video_url, duration, sort_order)
SELECT ch.id, v.title, v.content, v.video_url, v.duration, v.sort_order
FROM (VALUES
    ('IT101-01-2026A', 'Chương 1: Làm quen lập trình', 'Bài 1: Cài đặt môi trường', 'Hướng dẫn cài đặt IDE và chạy chương trình đầu tiên.', 'https://res.cloudinary.com/demo/video/upload/sample.mp4', 18, 1),
    ('IT101-01-2026A', 'Chương 2: Cấu trúc điều khiển', 'Bài 2: If Else và vòng lặp', 'Cách rẽ nhánh và lặp trong Java.', NULL, 25, 1),
    ('IT202-01-2026A', 'Chương 1: Mô hình quan hệ', 'Bài 1: Thiết kế ERD', 'Khái niệm thực thể, thuộc tính và quan hệ.', NULL, 30, 1),
    ('IT202-01-2026A', 'Chương 2: Truy vấn SQL', 'Bài 2: JOIN', 'INNER JOIN, LEFT JOIN và ví dụ thực tế.', NULL, 35, 1),
    ('BUS101-01-2026A', 'Chương 1: Tổng quan Marketing', 'Bài 1: Phân khúc thị trường', 'Phân đoạn khách hàng và mục tiêu.', NULL, 28, 1)
) AS v(class_code, chapter_title, title, content, video_url, duration, sort_order)
JOIN classes c ON c.class_code = v.class_code
JOIN chapters ch ON ch.clazz_id = c.id AND ch.title = v.chapter_title
ON CONFLICT DO NOTHING;

INSERT INTO announcements (class_id, title, content)
SELECT c.id, v.title, v.content
FROM (VALUES
    ('IT101-01-2026A', 'Lịch học tuần này', 'Lớp học sẽ diễn ra ở phòng A301 và cần mang laptop cá nhân.'),
    ('IT202-01-2026A', 'Nộp bài ERD', 'Nộp bài bằng định dạng PDF trước 23:59 thứ Bảy tuần này.'),
    ('BUS101-01-2026A', 'Thông báo thảo luận nhóm', 'Mỗi nhóm cần chuẩn bị slide 5 phút cho buổi trình bày tiếp theo.')
) AS v(class_code, title, content)
JOIN classes c ON c.class_code = v.class_code
ON CONFLICT DO NOTHING;

INSERT INTO notifications (recipient_id, type, title, content, reference_id, is_read)
SELECT u.id, v.type, v.title, v.content, NULL, v.is_read
FROM (VALUES
    ('sv20240001@student.edu.vn', 'NEW_ASSIGNMENT', 'Có bài tập mới', 'Bài tập 1 đã được giao cho lớp Nhập môn lập trình.', FALSE),
    ('sv20240001@student.edu.vn', 'NEW_GRADE', 'Thông báo điểm', 'Điểm bài quiz vừa được cập nhật.', TRUE),
    ('sv20240005@student.edu.vn', 'NEW_ANNOUNCEMENT', 'Thông báo lớp học', 'Lớp Marketing có tin nhắn mới từ giảng viên.', FALSE),
    ('gv.nguyenvana@learninghub.edu.vn', 'NEW_STUDENT', 'Sinh viên mới', 'Một sinh viên mới vừa đăng ký lớp của bạn.', FALSE)
) AS v(email, type, title, content, is_read)
JOIN users u ON u.email = v.email
ON CONFLICT DO NOTHING;

INSERT INTO forum_posts (clazz_id, author_id, title, content)
SELECT c.id, u.id, v.title, v.content
FROM (VALUES
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', 'Cần hỗ trợ bài tập đầu tiên', 'Mình chưa rõ cách sử dụng vòng lặp trong bài tập BMI. Ai có thể chia sẻ ý tưởng không?'),
    ('IT202-01-2026A', 'sv20240002@student.edu.vn', 'Mẫu ERD cho hệ thống LMS', 'Mình đã làm sơ bộ ERD, ai muốn cùng kiểm tra xem có cần thêm bảng nào không?'),
    ('BUS101-01-2026A', 'sv20240005@student.edu.vn', 'Thảo luận marketing', 'Mình đang làm case study về chiến dịch quảng cáo cho trường đại học, ai có đề xuất không?')
) AS v(class_code, email, title, content)
JOIN classes c ON c.class_code = v.class_code
JOIN users u ON u.email = v.email
ON CONFLICT DO NOTHING;

INSERT INTO forum_comments (post_id, author_id, content)
SELECT fp.id, u.id, v.content
FROM (VALUES
    ('IT101-01-2026A', 'Cần hỗ trợ bài tập đầu tiên', 'sv20240002@student.edu.vn', 'Bạn có thể tính BMI theo công thức cân nặng / chiều cao^2, rồi dùng if else để in kết quả.'),
    ('IT101-01-2026A', 'Cần hỗ trợ bài tập đầu tiên', 'gv.nguyenvana@learninghub.edu.vn', 'Đúng rồi, hãy đảm bảo kiểm tra đầu vào trước khi tính toán.'),
    ('IT202-01-2026A', 'Mẫu ERD cho hệ thống LMS', 'sv20240001@student.edu.vn', 'Nên thêm bảng CourseEnrollment nếu cần lưu lịch học phần của sinh viên.'),
    ('BUS101-01-2026A', 'Thảo luận marketing', 'gv.leminhc@learninghub.edu.vn', 'Bộ câu hỏi nên tập trung vào đối tượng và lợi ích của chương trình.')
) AS v(class_code, post_title, email, content)
JOIN classes c ON c.class_code = v.class_code
JOIN forum_posts fp ON fp.clazz_id = c.id AND fp.title = v.post_title
JOIN users u ON u.email = v.email
ON CONFLICT DO NOTHING;

INSERT INTO assignments (class_id, title, description, due_date, max_score)
SELECT c.id, v.title, v.description, CURRENT_TIMESTAMP + INTERVAL '7 days', v.max_score
FROM (VALUES
    ('IT101-01-2026A', 'Bài tập 1: BMI', 'Viết chương trình tính BMI và in kết quả theo từng mức.', 10.00),
    ('IT202-01-2026A', 'Bài tập 1: Thiết kế CSDL', 'Thiết kế sơ đồ ERD cho hệ thống LMS.', 10.00),
    ('BUS101-01-2026A', 'Case study: Chiến dịch truyền thông', 'Phân tích lựa chọn kênh truyền thông phù hợp cho trường đại học.', 10.00)
) AS v(class_code, title, description, max_score)
JOIN classes c ON c.class_code = v.class_code
ON CONFLICT DO NOTHING;

INSERT INTO submissions (assignment_id, student_id, file_url, submitted_at, score, is_late, feedback)
SELECT a.id, u.id, v.file_url, CURRENT_TIMESTAMP - INTERVAL '1 day', v.score, FALSE, v.feedback
FROM (VALUES
    ('IT101-01-2026A', 'Bài tập 1: BMI', 'sv20240001@student.edu.vn', 'https://example.com/submissions/sv20240001-bmi.pdf', 8.50, 'Bài làm đạt yêu cầu, cần giải thích rõ hơn.'),
    ('IT202-01-2026A', 'Bài tập 1: Thiết kế CSDL', 'sv20240001@student.edu.vn', 'https://example.com/submissions/sv20240001-erd.pdf', 8.00, 'ERD khá tốt, nên thêm mô tả rõ hơn về quan hệ.')
) AS v(class_code, assignment_title, email, file_url, score, feedback)
JOIN classes c ON c.class_code = v.class_code
JOIN assignments a ON a.class_id = c.id AND a.title = v.assignment_title
JOIN users u ON u.email = v.email
ON CONFLICT DO NOTHING;

INSERT INTO quizzes (class_id, title, duration_minutes, total_score)
SELECT c.id, v.title, v.duration_minutes, v.total_score
FROM (VALUES
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 20, 10.00),
    ('IT202-01-2026A', 'Quiz 1: Khóa và quan hệ', 25, 10.00),
    ('BUS101-01-2026A', 'Quiz 1: Marketing căn bản', 15, 10.00)
) AS v(class_code, title, duration_minutes, total_score)
JOIN classes c ON c.class_code = v.class_code
ON CONFLICT DO NOTHING;

INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer)
SELECT q.id, v.question_text, v.option_a, v.option_b, v.option_c, v.option_d, v.correct_answer
FROM (VALUES
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 'Kiểu dữ liệu nào dùng để lưu đúng/sai?', 'String', 'Boolean', 'Integer', 'Double', 'B'),
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 'Cấu trúc nào dùng để rẽ nhánh chương trình?', 'for', 'while', 'if/else', 'class', 'C'),
    ('IT202-01-2026A', 'Quiz 1: Khóa và quan hệ', 'Khóa chính dùng để làm gì?', 'Mã hóa dữ liệu', 'Định danh duy nhất một dòng', 'Sắp xếp bảng', 'Xóa bảng', 'B'),
    ('BUS101-01-2026A', 'Quiz 1: Marketing căn bản', 'STP gồm những bước nào?', 'Sell-Test-Plan', 'Segment-Target-Position', 'Search-Track-Publish', 'Study-Train-Practice', 'B')
) AS v(class_code, quiz_title, question_text, option_a, option_b, option_c, option_d, correct_answer)
JOIN classes c ON c.class_code = v.class_code
JOIN quizzes q ON q.class_id = c.id AND q.title = v.quiz_title
ON CONFLICT DO NOTHING;

INSERT INTO quiz_attempts (quiz_id, student_id, score, started_at, submitted_at)
SELECT q.id, u.id, v.score, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '18 minutes'
FROM (VALUES
    ('IT101-01-2026A', 'Quiz 1: Biến và điều kiện', 'sv20240001@student.edu.vn', 8.50),
    ('IT202-01-2026A', 'Quiz 1: Khóa và quan hệ', 'sv20240001@student.edu.vn', 7.50),
    ('BUS101-01-2026A', 'Quiz 1: Marketing căn bản', 'sv20240005@student.edu.vn', 8.50)
) AS v(class_code, quiz_title, email, score)
JOIN classes c ON c.class_code = v.class_code
JOIN quizzes q ON q.class_id = c.id AND q.title = v.quiz_title
JOIN users u ON u.email = v.email
ON CONFLICT DO NOTHING;

INSERT INTO grades (class_id, student_id, midterm_score, final_score, total_score)
SELECT c.id, u.id, v.midterm_score, v.final_score, v.total_score
FROM (VALUES
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', 8.50, 8.80, 8.65),
    ('IT101-01-2026A', 'sv20240002@student.edu.vn', 7.80, 8.10, 7.95),
    ('IT202-01-2026A', 'sv20240001@student.edu.vn', 8.00, 8.50, 8.25),
    ('BUS101-01-2026A', 'sv20240005@student.edu.vn', 8.60, 9.00, 8.80)
) AS v(class_code, email, midterm_score, final_score, total_score)
JOIN classes c ON c.class_code = v.class_code
JOIN users u ON u.email = v.email
ON CONFLICT (class_id, student_id) DO NOTHING;

INSERT INTO attendance (class_id, student_id, attendance_date, status)
SELECT c.id, u.id, CURRENT_DATE - 7, v.status
FROM (VALUES
    ('IT101-01-2026A', 'sv20240001@student.edu.vn', 'PRESENT'),
    ('IT101-01-2026A', 'sv20240002@student.edu.vn', 'LATE'),
    ('IT202-01-2026A', 'sv20240001@student.edu.vn', 'PRESENT'),
    ('BUS101-01-2026A', 'sv20240005@student.edu.vn', 'PRESENT')
) AS v(class_code, email, status)
JOIN classes c ON c.class_code = v.class_code
JOIN users u ON u.email = v.email
ON CONFLICT (class_id, student_id, attendance_date) DO NOTHING;

INSERT INTO registration_periods (name, semester, academic_year, open_at, close_at, max_credits, is_active)
VALUES
    ('Đợt đăng ký HK1 2026-2027', 'HK1', '2026-2027', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '20 days', 24, TRUE),
    ('Đợt đăng ký HK2 2026-2027', 'HK2', '2026-2027', CURRENT_TIMESTAMP + INTERVAL '30 days', CURRENT_TIMESTAMP + INTERVAL '60 days', 24, FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO curricula (name, faculty, academic_year, is_active)
VALUES
    ('Chương trình CNTT K65', 'Công nghệ thông tin', '2024-2028', TRUE),
    ('Chương trình QTKD K65', 'Quản trị kinh doanh', '2024-2028', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO curriculum_courses (curriculum_id, course_id, semester_no, is_required)
SELECT cur.id, co.id, v.semester_no, v.is_required
FROM (VALUES
    ('Chương trình CNTT K65', 'IT101', 1, TRUE),
    ('Chương trình CNTT K65', 'IT202', 2, TRUE),
    ('Chương trình CNTT K65', 'IT303', 5, TRUE),
    ('Chương trình QTKD K65', 'BUS101', 1, TRUE)
) AS v(curriculum_name, course_code, semester_no, is_required)
JOIN curricula cur ON cur.name = v.curriculum_name
JOIN courses co ON co.code = v.course_code
ON CONFLICT DO NOTHING;

INSERT INTO course_prerequisites (course_id, prerequisite_course_id)
SELECT co.id, pre.id
FROM courses co
JOIN courses pre ON pre.code = 'IT101'
WHERE co.code = 'IT202'
ON CONFLICT DO NOTHING;
