# 🎓 LearningHub - Modular Monolith University LMS

LearningHub là Hệ thống Quản lý Học tập (Learning Management System - LMS) thiết kế theo kiến trúc **Modular Monolith** dành cho Trường Đại học / Học viện. Hệ thống hỗ trợ đầy đủ các quy trình đào tạo, quản lý khóa học, điểm danh, thi/trắc nghiệm, chấm điểm và đăng ký tín chỉ.

---

## 🛠️ Công nghệ sử dụng (Tech Stack)

- **Language & Framework**: Java 17, Spring Boot 3.5.x
- **Security & Auth**: Spring Security, JWT Authentication (HS256)
- **Database & Migration**: PostgreSQL 16, Spring Data JPA, Flyway (DB Migration)
- **Media & File Storage**: Cloudinary (Video / Document Storage)
- **Email Service**: Spring Mail, Brevo SMTP
- **DevOps & Container**: Docker, Docker Compose, GitHub Actions (CI/CD)
- **Build Tool**: Apache Maven

---

## 🏗️ Kiến trúc các Module (Modular Monolith)

Hệ thống được chia làm các module độc lập theo miền nghiệp vụ (`com.ex.learninghub.modules`):

- 👤 **`user`**: Quản lý tài khoản (Admin, Teacher, Student), phân quyền, hồ sơ người dùng.
- 📚 **`academic`**: Chương trình khung, học phần, môn học, lớp học phần.
- 📝 **`assessment`**: Bài tập (Assignment), nộp bài (Submission) và chấm điểm tự động / thủ công.
- ❓ **`quiz`**: Ngân hàng câu hỏi, tạo đề thi trắc nghiệm và thực hiện bài làm.
- 📊 **`grading`**: Thang điểm, thành phần điểm, bảng điểm học phần và tổng kết GPA/CPA.
- 📅 **`attendance`**: Quản lý điểm danh sinh viên, tự động cảnh báo cấm thi khi vắng quá tỷ lệ quy định.
- 📑 **`registration`**: Đợt đăng ký tín chỉ, đăng ký học phần và xử lý ràng buộc tín chỉ / cảnh báo học tập.
- 🔔 **`notification`**: Thông báo qua Email & System Notification.

---

## 🚀 Hướng dẫn chạy dự án (Quick Start)

### 1. Yêu cầu môi trường (Prerequisites)
- Java OpenJDK 17 trở lên
- Maven 3.8+ (hoặc dùng `./mvnw` có sẵn)
- Docker & Docker Compose (cho cơ sở dữ liệu)

### 2. Thiết lập biến môi trường
Sao chép file cấu hình mẫu `.env.example` thành `.env`:
```bash
cp .env.example .env
```

### 3. Khởi chạy bằng Docker Compose (Khuyên dùng)
Chạy toàn bộ dịch vụ (Database PostgreSQL + Redis + Backend Spring Boot):
```bash
docker-compose up -d
```
Ứng dụng backend sẽ khởi chạy tại: `http://localhost:8080/api/v1`

### 4. Khởi chạy thủ công bằng Maven (Môi trường Dev)
Môi trường `dev` sử dụng H2 Database (in-memory) tự động:
```bash
# Biên dịch và chạy ứng dụng
./mvnw spring-boot:run
```

---

## 🧪 Kiểm thử & Đóng gói (Testing & Build)

### Chạy Unit Test & Integration Test
```bash
./mvnw test
```

### Đóng gói file JAR
```bash
./mvnw clean package -DskipTests
```
File JAR đầu ra: `target/learninghub-0.0.1-SNAPSHOT.jar`

---

## ⚙️ Cấu hình biến môi trường chính (`.env`)

| Biến môi trường | Mô tả | Giá trị mặc định |
|---|---|---|
| `SPRING_PROFILE` | Spring profile (`dev` / `prod`) | `dev` |
| `POSTGRES_HOST` | Địa chỉ PostgreSQL host | `localhost` |
| `POSTGRES_PORT` | Cổng PostgreSQL | `5432` |
| `POSTGRES_DB` | Tên Database | `learninghub` |
| `JWT_SECRET` | Khóa bí mật mã hóa JWT (Tối thiểu 32 ký tự) | *xem .env.example* |
| `CLOUDINARY_CLOUD_NAME` | Cloud Name tài khoản Cloudinary | - |
| `ATTENDANCE_MAX_ABSENT_RATIO` | Tỷ lệ vắng mặt tối đa trước khi bị cấm thi | `0.2` (20%) |
| `REG_MAX_CREDITS_PROBATION` | Tín chỉ tối đa cho sinh viên bị cảnh báo | `14` |

---

## 📄 Giấy phép (License) & Tác giả
- Đồ án Tốt nghiệp Đại học (DATN)
- Phát triển bởi **LearningHub Team**
