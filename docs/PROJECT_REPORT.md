# LearningHub — Báo cáo Đánh giá & Nghiệm thu Dự án (Project Execution Report)

**Phiên bản:** 3.0  
**Ngày cập nhật:** Tháng 09/2026  
**Trạng thái hệ thống:** Hoàn thành 100% — Sẵn sàng Bảo vệ Đồ án & Triển khai Enterprise (Defense & Production Ready)  
**Trạng thái kiểm thử tự động:** ✅ **107/107 Unit Tests PASS 100%** (0 Failure, 0 Error).

---

## 1. TỔNG QUAN DỰ ÁN

**LearningHub** là Nền tảng Quản lý Học tập & Đào tạo Tín chỉ Đại học (LMS) hiện đại, tích hợp Trí tuệ Nhân tạo (AI), Điểm danh QR Code động mã hóa OTP, Cảnh báo rủi ro học vụ sớm và Tường lửa bảo mật WAF. Hệ thống được thiết kế theo kiến trúc phân lớp chuẩn doanh nghiệp, phục vụ 3 nhóm đối tượng người dùng chính: **Sinh viên (STUDENT)**, **Giảng viên (LECTURER)** và **Quản trị viên (ADMIN)**.

- **Backend Stack:** Java 17 LTS, Spring Boot 3.5.x, Spring Security 6, JWT, Redis 7 (Caching, Token Blacklist, Sliding-Window WAF), PostgreSQL 16 (Flyway V1–V11), Spring WebSocket (STOMP), Cloudinary SDK, GraphQL, Micrometer Actuator APM.
- **Frontend Stack:** React 19, TypeScript, Vite 8, TailwindCSS, Axios Interceptors.
- **Bộ kiểm thử tự động:** 107/107 Unit Tests PASS (Spring Boot Test & Mockito).

---

## 2. KIẾN TRÚC & QUYẾT ĐỊNH THIẾT KẾ (ARCHITECTURE)

```
┌────────────────────────────────────────────────────────────────────────┐
│                          CLIENT LAYER                                  │
│  React 19 / TypeScript App (Vite 8)  │  RESTful API / STOMP / GraphQL  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTPS / WSS
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        SPRING BOOT BACKEND API                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Auth &   │  │ Course & │  │Enrollment│  │ Grading  │  │ Quiz AI  │  │
│  │ Security │  │ Content  │  │ & Tuition│  │ & Risk   │  │ Module   │  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
│       │             │             │             │             │        │
│  ┌────┴─────────────┴─────────────┴─────────────┴─────────────┴─────┐  │
│  │                     CROSS-CUTTING SERVICES                       │  │
│  │ Redis Cache │ WAF Rate Limiter │ Async Event Queue │ GraphQL Engine │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
               ┌────────────────────┼────────────────────┐
               ▼                    ▼                    ▼
        ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
        │  PostgreSQL  │     │ Redis Server │     │  Cloudinary  │
        │(Flyway V1-V11)     │  (Cache/WAF) │     │ (Media/Video)│
        └──────────────┘     └──────────────┘     └──────────────┘
```

---

## 3. DANH SÁCH 20 TÍNH NĂNG & MODULE CAO CẤP ĐÃ HOÀN THÀNH

1. **AI Question Generator Service**: Tự động trích xuất nội dung bài giảng và tạo ngân hàng câu hỏi trắc nghiệm 4 lựa chọn bằng AI.
2. **Dynamic QR Code OTP Attendance**: Điểm danh tự động bằng mã QR Code động với mã OTP thay đổi liên tục mỗi 10s.
3. **Academic Risk Early Warning Engine**: Dự báo sớm nguy cơ bị cấm thi hoặc học lại (`SAFE`, `WARNING`, `CRITICAL`) dựa trên tỷ lệ vắng mặt và điểm số.
4. **Google OAuth2 Single Sign-On**: Đăng nhập 1-Click bằng tài khoản Google.
5. **TOTP Two-Factor Authentication (2FA)**: Xác thực 2 bước tăng cường an toàn tài khoản Quản trị & Giảng viên qua Google Authenticator.
6. **Redis-based JWT Token Blacklist**: Vô hiệu hóa Token ngay lập tức khi Logout hoặc đổi mật khẩu với cơ chế TTL tự động xóa.
7. **Distributed Redis Caching Layer**: Tầng Cache nâng cao cho thông tin Khóa học, Lớp học và Chương trình đào tạo (`@Cacheable`).
8. **Event-Driven Async Architecture**: Xử lý sự kiện ngầm (`@Async`, `@EventListener`) gửi mail và ghi nhận nhật ký Audit.
9. **STOMP WebSocket Real-time Notifications**: Đẩy thông báo điểm số, lớp học tức thời tới giao diện người dùng.
10. **Report Exporter (PDF Transcript & Excel Scores)**: Xuất báo cáo điểm lớp học phần dạng Excel và Bảng điểm tổng kết dạng PDF.
11. **Cloudinary Cloud Storage Integration**: Tải lên bài tập, slide và video bài học lên hạ tầng đám mây Cloudinary.
12. **Jaccard Similarity Plagiarism Checker**: Kiểm tra và cảnh báo mức độ sao chép/đạo văn bài tập giữa các sinh viên.
13. **Universal Full-Text Fuzzy Search**: Bộ máy tìm kiếm toàn văn tìm kiếm nhanh bài giảng, diễn đàn và khóa học.
14. **System Metrics Monitoring (Actuator & Micrometer)**: Giám sát thời gian thực tài nguyên CPU, RAM JVM, Health-check và lượng Request/sec.
15. **Redis Sliding-Window Rate Limiter WAF**: Tường lửa ngăn chặn tấn công DoS / Brute-force mật khẩu (Giới hạn tốc độ Request).
16. **Microsoft 365 Azure AD OIDC SSO**: Tích hợp đăng nhập bằng tài khoản sinh viên/giảng viên Office 365 Microsoft Azure AD.
17. **High-Throughput Async Task Queue**: Hàng đợi xử lý các tác vụ nặng (tính điểm hàng loạt, import dữ liệu sinh viên).
18. **Dynamic GraphQL Execution Endpoint**: Cổng truy vấn dữ liệu linh hoạt GraphQL tùy chọn các trường trả về.
19. **Multi-Campus Tenant Scoping**: Kiến trúc đa phân hiệu/cơ sở hỗ trợ mở rộng tổ chức đào tạo đa cơ sở.
20. **Database Migration Audit & Backup Service**: Sao lưu cơ sở dữ liệu và quản lý tự động lịch sử di trú Flyway V1–V11.

---

## 4. CHI TIẾT DANH MỤC FLYWAY MIGRATIONS (DB V1 – V11)

| Version | Migration File | Mô tả chi tiết |
| :---: | :--- | :--- |
| **V1** | `V1__init_schema.sql` | Khởi tạo cấu trúc bảng chính (users, courses, clazzes, enrollments, grades). |
| **V2** | `V2__fix_class_schedule_day_of_week_type.sql` | Sửa định dạng lịch học và thứ trong tuần. |
| **V3** | `V3__add_missing_audit_columns.sql` | Bổ sung các cột audit thời gian tạo và cập nhật. |
| **V4** | `V4__seed_demo_data_account.sql` | Dữ liệu tài khoản demo hệ thống. |
| **V5** | `V5__seed_demo_data.sql` | Dữ liệu mẫu lớp học, điểm số và bài tập. |
| **V6** | `V6__add_rbac_and_grading_policy.sql` | Bảng phân quyền RBAC và cấu hình trọng số GPA theo CTĐT. |
| **V7** | `V7__create_video_learning_tables.sql` | Bảng theo dõi tiến độ xem video và câu hỏi tương tác. |
| **V8** | `V8__add_submission_multi_type.sql` | Hỗ trợ nộp nhiều loại bài tập khác nhau. |
| **V9** | `V9__add_lecturer_class_permissions.sql` | Bảng phân quyền ủy quyền giảng viên trợ giảng theo lớp. |
| **V10** | `V10__add_audit_logs.sql` | Bảng nhật ký Audit Log hệ thống ghi lại thao tác nhạy cảm. |
| **V11** | `V11__add_semester_to_enrollment.sql` | Bổ sung học kỳ (`semester`) và năm học (`academic_year`) vào bảng `enrollments`. |

---

## 5. KẾT QUẢ ĐÁNH GIÁ KIỂM THỬ (TEST RESULTS)

- **Backend Unit Tests:** **107 / 107 Passed (100%)**
- **Frontend Build:** **Vite 8 Build SUCCESS** (0 TypeScript Errors, build trong ~650ms).
- **Security Audit:** Đạt các tiêu chuẩn bảo mật an toàn thông tin OWASP Top 10.
