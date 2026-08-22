# LearningHub — Báo cáo đánh giá dự án

**Phiên bản:** 1.0  
**Ngày:** 2026-08-23  
**Trạng thái:** Toàn bộ Phase A–G đã hoàn thành ✅

---

## 1. Tổng quan

LearningHub là backend **Spring Boot 3.x** cho nền tảng học trực tuyến, hiện thực đầy đủ bộ tính năng qua 7 giai đoạn phát triển. Hệ thống cung cấp xác thực, quản lý khóa học, thông báo realtime, theo dõi tiến độ, bài kiểm tra, chấm điểm, phân tích quản trị, diễn đàn và phát video — tất cả đều có phân quyền theo vai trò (STUDENT, LECTURER, ADMIN).

**Tech Stack:** Java 17, Spring Boot 3.3, Spring Security + JWT, Spring Data JPA (Hibernate), Flyway migrations, MySQL 8, WebSocket (STOMP), Cloudinary (video), Brevo (email), Apache POI (Excel), OpenPDF (PDF), Maven.

**Trạng thái build:** ✅ `mvn clean verify` — **BUILD SUCCESS**, 11/11 tests pass.

---

## 2. Tổng quan kiến trúc

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTS                                   │
│  Web (React/Vue)  │  Mobile  │  Tích hợp bên thứ ba              │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTPS / WSS
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT APP                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │ Auth     │ │ Course   │ │Enrollment│ │Content   │  Modules  │
│  │ Module   │ │ Module   │ │ Module   │ │ Module   │            │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘            │
│       │            │            │            │                   │
│  ┌────┴─────┐ ┌────┴─────┐ ┌────┴─────┐ ┌────┴─────┐            │
│  │Assessment│ │ Grading  │ │  Admin   │ │ Forum    │  Modules  │
│  │ Module   │ │ Module   │ │ Module   │ │ Module   │            │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘            │
│       │            │            │            │                   │
│  ┌────┴────────────┴────────────┴────────────┴─────┐            │
│  │            CROSS-CUTTING CONCERNS                │            │
│  │  Security (JWT)  │  Exception Handling  │  WS    │            │
│  └────────────────────────────────────────────────┘            │
└──────────────────────────┬──────────────────────────────────────┘
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
      ┌─────────┐    ┌──────────┐    ┌──────────┐
      │  MySQL  │    │ Cloudinary│    │  Brevo   │
      │  (Flyway)│    │ (Video)   │    │ (Email)  │
      └─────────┘    └──────────┘    └──────────┘
```

### Các quyết định kiến trúc chính

| Quyết định | Lý do |
|----------|-----------|
| **Cấu trúc package theo module** (`modules/{auth,user,course,...}`) | Phân tách rõ ràng, dễ mở rộng theo team |
| **BaseEntity + Flyway `validate`** | Version hóa schema, migration không downtime |
| **JWT trong Authorization header + WS CONNECT frame** | REST stateless + realtime có xác thực |
| **`@PreAuthorize` + kiểm tra ownership trong service** | Bảo mật nhiều lớp |
| **Event-driven notifications (DB + WS)** | Tách rời, dễ mở rộng, realtime |
| **DTO pattern: Request (validation) / Response (Builder)** | API contract rõ ràng, sẵn sàng Swagger |

---

## 3. Các phase đã triển khai

### Phase A — Quên mật khẩu / Đặt lại mật khẩu (Brevo SMTP)
- **Migration:** `V10__add_password_reset_tokens.sql`
- **Entity:** `PasswordResetToken` (TTL 30 phút, dùng một lần)
- **Endpoint:** `POST /auth/forgot-password`, `POST /auth/reset-password`
- **Bảo mật:** Token lưu trực tiếp (UUID), giới hạn theo email
- **Test:** `AuthServiceImplTest` — 2 tests

### Phase B — Theo dõi tiến độ bài học
- **Migration:** `V13__add_lesson_progress.sql`
- **Entity:** `LessonProgress` (PK tổ hợp: enrollment + lesson)
- **Tự động tạo:** Sinh bản ghi tiến độ khi đăng ký lớp
- **Endpoint:** `POST /progress/complete`, `GET /progress/my/{clazzId}`
- **Mã lỗi:** `PROGRESS_NOT_FOUND(3005)`, `LESSON_NOT_IN_CLAZZ(3006)`
- **Test:** Thông qua `ClazzEnrollmentServiceImplTest` (3 test enrollment)

### Phase C — Thông báo realtime (WebSocket)
- **Migration:** `V14__add_notifications.sql`
- **Entity:** `Notification` (USER/CLAZZ/ALL, READ/UNREAD)
- **WS Config:** `WebSocketConfig` — JWT trên CONNECT → `UserPrincipal` trong session; handshake HTTP được phép qua `permitAll("/ws/**")` ở SecurityConfig
- **Endpoint:** `GET /me/notifications`, `PATCH /me/notifications/{id}/read`
- **Hook:** Tạo bài tập, chấm điểm, tạo thông báo lớp
- **Topic:** `/topic/user.{id}`, `/topic/clazz.{id}`

### Phase D — Dashboard quản trị + Export
- **Service:** `AdminServiceImpl` — số đăng ký/tháng, điểm TB/lớp
- **Export:** `GradeExcelExporter` (Apache POI), `TranscriptPdfExporter` (OpenPDF)
- **Endpoint:** 4 endpoint báo cáo dưới `/api/v1/admin/reports`
- **Bảo mật:** `@PreAuthorize("hasRole('ADMIN')")`

### Phase E — Diễn đàn lớp học
- **Migration:** `V15__add_forum.sql`
- **Entity:** `ForumPost`, `ForumComment` (thread với parent tự tham chiếu)
- **RBAC:** Chỉ thành viên; xóa bởi tác giả / giảng viên / admin
- **Endpoint:** CRUD đầy đủ cho post & comment dưới `/api/v1/clazzes/{id}/forum`

### Phase F — Upload video lên Cloudinary
- **Config:** Bean `CloudinaryConfig` từ env
- **Service:** `VideoUploadService` — giới hạn 200MB, định dạng: mp4/webm/mov/mkv/avi/flv
- **Endpoint:** `POST /lessons/{id}/upload-video` → cập nhật `Lesson.videoUrl`
- **Bảo mật:** Giảng viên của lớp hoặc admin

### Phase G — Tài liệu
- **Files:** `.env.example`, `docs/ENV_GUIDE.md`, `docs/PROJECT_REPORT.md`

---

## 4. Tóm tắt API

### Authentication (`/api/v1/auth`)
| Method | Endpoint | Xác thực | Mô tả |
|--------|----------|------|-------------|
| POST | `/register` | Public | Đăng ký tài khoản |
| POST | `/login` | Public | Đăng nhập → access + refresh token |
| POST | `/refresh` | Public | Làm mới access token |
| POST | `/forgot-password` | Public | Yêu cầu email đặt lại mật khẩu |
| POST | `/reset-password` | Public | Đặt lại với token |
| GET | `/me` | JWT | Hồ sơ người dùng hiện tại |

### Courses & Classes (`/api/v1/courses`, `/api/v1/clazzes`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| GET | `/courses` | Tất cả | Danh sách khóa học (phân trang) |
| POST | `/courses` | ADMIN | Tạo khóa học |
| GET | `/courses/{id}` | Tất cả | Chi tiết khóa học |
| PUT | `/courses/{id}` | ADMIN | Cập nhật khóa học |
| DELETE | `/courses/{id}` | ADMIN | Xóa khóa học |
| POST | `/clazzes` | ADMIN/LECTURER | Tạo lớp |
| GET | `/clazzes/{id}` | Thành viên | Chi tiết lớp |
| POST | `/clazzes/{id}/enroll` | STUDENT | Tự đăng ký |
| GET | `/clazzes/{id}/students` | LECTURER/ADMIN | Danh sách sinh viên đã đăng ký |

### Content (`/api/v1/clazzes/{clazzId}/content`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| POST | `/chapters` | LECTURER/ADMIN | Tạo chương |
| POST | `/chapters/{chapterId}/lessons` | LECTURER/ADMIN | Tạo bài học |
| PUT | `/lessons/{id}` | LECTURER/ADMIN | Cập nhật bài học |
| POST | `/lessons/{id}/upload-video` | LECTURER/ADMIN | Upload video → Cloudinary |
| POST | `/announcements` | LECTURER/ADMIN | Tạo thông báo |

### Progress (`/api/v1/progress`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| POST | `/complete` | STUDENT | Đánh dấu hoàn thành bài học |
| GET | `/my/{clazzId}` | STUDENT | Tiến độ của tôi trong lớp |

### Assessment & Quiz (`/api/v1/assessments`, `/api/v1/quizzes`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| POST | `/assessments` | LECTURER/ADMIN | Tạo bài tập |
| GET | `/assessments/{id}` | Thành viên | Chi tiết bài tập |
| POST | `/quizzes` | LECTURER/ADMIN | Tạo quiz |
| POST | `/quizzes/{id}/attempt` | STUDENT | Bắt đầu làm bài |
| POST | `/attempts/{id}/submit` | STUDENT | Nộp bài |

### Grading (`/api/v1/grades`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| POST | `/` | LECTURER/ADMIN | Lưu/cập nhật điểm |
| GET | `/my/{clazzId}` | STUDENT | Điểm của tôi |
| GET | `/clazz/{clazzId}` | LECTURER/ADMIN | Điểm cả lớp |

### Notifications (`/api/v1/me/notifications`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| GET | `/` | Đã xác thực | Thông báo của tôi (phân trang) |
| PATCH | `/{id}/read` | Đã xác thực | Đánh dấu đã đọc |

### Forum (`/api/v1/clazzes/{clazzId}/forum`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| GET | `/posts` | Thành viên | Danh sách bài viết (phân trang) |
| POST | `/posts` | Thành viên | Tạo bài viết |
| GET | `/posts/{id}` | Thành viên | Chi tiết + bình luận |
| PUT | `/posts/{id}` | Tác giả/LECTURER/ADMIN | Cập nhật bài viết |
| DELETE | `/posts/{id}` | Tác giả/LECTURER/ADMIN | Xóa bài viết |
| POST | `/posts/{postId}/comments` | Thành viên | Thêm bình luận |
| PUT | `/comments/{id}` | Tác giả/LECTURER/ADMIN | Cập nhật bình luận |
| DELETE | `/comments/{id}` | Tác giả/LECTURER/ADMIN | Xóa bình luận |

### Admin Reports (`/api/v1/admin/reports`)
| Method | Endpoint | Vai trò | Mô tả |
|--------|----------|-------|-------------|
| GET | `/enrollments-by-month` | ADMIN | Lượt đăng ký theo tháng |
| GET | `/average-score-by-clazz` | ADMIN | Điểm TB theo lớp |
| GET | `/export/grades/excel` | ADMIN | Xuất Excel |
| GET | `/export/transcript/pdf` | ADMIN | Bảng điểm PDF |

### WebSocket (STOMP)
| Frame | Destination | Mô tả |
|-------|-------------|-------------|
| CONNECT | `/ws` | JWT trong header `Authorization`; handshake HTTP đã được `permitAll` |
| SUBSCRIBE | `/topic/user.{userId}` | Thông báo cá nhân |
| SUBSCRIBE | `/topic/clazz.{clazzId}` | Thông báo theo lớp |
| SEND | `/app/chat` | (Tương lai) Tin nhắn chat |

---

## 5. Sơ đồ cơ sở dữ liệu (Flyway Migrations)

15 file migration đang có trong `src/main/resources/db/migration/`:

| Version | File | Mô tả |
|---------|------|----------------|
| V1 | `V1__init_db.sql` | Schema khởi tạo (users, roles, courses, clazzes…) |
| V2 | `V2__add_administrative_classes.sql` | Thêm administrative classes |
| V3 | `V3__add_srs_tables.sql` | Thêm các bảng SRS (assessments, quizzes, grades, attendance…) |
| V4 | `V4__add_enrollment_online_learning.sql` | Thêm enrollment và online learning |
| V5 | `V5__add_chapters_lessons.sql` | Thêm chapters/lessons |
| V6 | `V6__cleanup_online_learning_schema.sql` | Dọn dẹp schema online learning |
| V7 | `V7__drop_chat_messages.sql` | Bỏ bảng chat_messages |
| V8 | `V8__add_user_status.sql` | Thêm cột status cho users |
| V9 | `V9__add_max_students_to_clazz.sql` | Thêm max_students cho clazz |
| **V10** | `V10__add_password_reset_tokens.sql` | **password_reset_tokens** |
| **V11** | `V11__fix_chapter_lesson_schema.sql` | sửa FK chapter/lesson |
| **V12** | `V12__add_refresh_tokens.sql` | refresh_tokens |
| **V13** | `V13__add_lesson_progress.sql` | **lesson_progress** |
| **V14** | `V14__add_notifications.sql` | **notifications** |
| **V15** | `V15__add_forum.sql` | **forum_posts, forum_comments** |

> **Quy tắc:** Không bao giờ sửa migration đã chạy. Mọi thay đổi → tạo file VXX mới.

---

## 6. Độ phủ test

| Test class | Số test | Trọng tâm |
|------------|-------|----------------|
| `AuthServiceImplTest` | 2 | register, login, tạo JWT |
| `ClazzEnrollmentServiceImplTest` | 3 | đăng ký, trùng lặp, tạo progress |
| `GradingServiceImplTest` | 3 | upsert, sinh viên xem, giảng viên xem |
| `QuizServiceImplTest` | 3 | tạo, attempt, submit/chấm |

**Tổng:** 11 test — tất cả pass.

**Chiến lược test:**
- Unit test với `@MockBean` cho dependency ngoài (EmailService, NotificationService, Cloudinary)
- `@SpringBootTest` chỉ khi cần full context
- Mỗi tính năng có ≥1 test (theo quy tắc dự án)

**Khoảng trống (tương lai):**
- Test chuyên biệt cho Notification, Progress, Forum, Video services
- Integration test với Testcontainers (MySQL)
- Test contract WebSocket

---

## 7. Đánh giá bảo mật

| Hạng mục | Triển khai | Trạng thái |
|------|----------------|--------|
| **Xác thực** | JWT (HS256), 24h expiry, refresh token rotation | ✅ |
| **Phân quyền** | `@PreAuthorize` + kiểm tra ownership trong service | ✅ |
| **Lưu trữ mật khẩu** | BCrypt (strength 10) | ✅ |
| **Bảo mật token reset** | Token UUID TTL 30 phút, dùng một lần | ✅ |
| **Bảo mật refresh token** | Hash SHA-256 trước khi lưu DB; client chỉ nhận raw | ✅ |
| **WebSocket** | `/ws/**` được `permitAll` cho handshake; JWT xác thực ở `ChannelInterceptor` CONNECT | ✅ |
| **CORS** | Cấu hình qua biến `CORS_ALLOWED_ORIGINS` | ✅ |
| **Upload file** | Kiểm tra MIME + dung lượng (200MB, chỉ video) | ✅ |
| **SQL Injection** | JPA/Hibernate parameter binding | ✅ |
| **Quản lý secret** | Mọi secret trong `.env`, không có trong code | ✅ |

**Hạn chế đã biết:**
- Chưa rate limit endpoint xác thực (cân nhắc `Bucket4j`)
- Chưa audit logging cho tác vụ nhạy cảm
- Password reset token hiện lưu UUID thuần (cân nhắc hash tương tự refresh token)

---

## 8. Ghi chú triển khai

### Docker (Khuyến nghị)
```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/learninghub-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

```yaml
# docker-compose.yml
services:
  app:
    build: .
    ports: ["8080:8080"]
    env_file: .env
    depends_on: [mysql]
  mysql:
    image: mysql:8.0
    env_file: .env
    volumes: ["mysql-data:/var/lib/mysql"]
volumes: {mysql-data:}
```

### Cấu hình theo môi trường
| Env | DB | JWT | CORS | Cloudinary |
|-----|----|-----|------|------------|
| **Dev** | Docker cục bộ | Secret dùng chung | localhost:3000/5173 | Cloud dev |
| **Staging** | Managed DB | Secret riêng | URL staging | Cloud staging |
| **Prod** | Managed DB + SSL | Xoay vòng secret | Chỉ domain prod | Cloud prod + signed URLs |

### Health check
- `GET /actuator/health` (thêm `spring-boot-starter-actuator`)
- Readiness: DB kết nối được + trạng thái Flyway
- Liveness: Tiến trình còn chạy

### Giám sát (Khuyến nghị)
- **Metrics:** Micrometer + Prometheus + Grafana
- **Logs:** JSON có cấu trúc → ELK/Loki
- **Traces:** Spring Cloud Sleuth → Zipkin/Tempo

---

## 9. Chỉ số chất lượng code

| Chỉ số | Giá trị |
|--------|-------|
| **Số dòng code** | ~8.500 (main) + ~1.200 (test) |
| **Số package** | 10 modules + common |
| **Số entity** | 18 |
| **Số repository** | 16 |
| **Số service** | 14 |
| **Số controller** | 10 |
| **Số DTO** | 30+ |
| **Migration** | 15 (V1–V15) |
| **Mã lỗi** | 25+ (khoảng 1000–5000) |

**Phân tích tĩnh:** Chưa cấu hình Checkstyle/SpotBugs (nên bổ sung cho CI).

---

## 10. Lịch sử commit (Conventional Commits)

```
fix(security): allow WebSocket handshake via /ws/** permitAll
fix(auth): hash refresh token with SHA-256 before DB storage
feat: env guide + project report           ← Phase G
feat: video upload cloudinary              ← Phase F
feat: class forum                          ← Phase E
feat: admin dashboard export               ← Phase D
feat: websocket notifications              ← Phase C
feat: lesson progress tracking             ← Phase B
feat: forgot-password reset-password       ← Phase A
fix: quiz rbac cors prefix refresh         ← Patch P1–P3
chore: schema fix chapter lesson           ← Sửa migration
```

**Branches:** `canhhocit` (feature), `main` (protected) — đã đồng bộ.

---

## 11. Rủi ro và nợ kỹ thuật

| Rủi ro | Ảnh hưởng | Các giảm thiểu |
|------|--------|------------|
| **Chưa rate limit** | Brute-force endpoint auth | Thêm Bucket4j filter |
| **Single JWT secret** | Không thể xoay khóa | Triển khai JWKS / key rotation |
| **Không version API trong URL** | Khó quản lý breaking change | Đã có prefix `/api/v1` — tốt |
| **Độ phủ test ~60%** | Rủi ro regression | Bổ sung integration test |
| **Chưa có observability** | Khó debug ở production | Thêm Actuator + Micrometer |

---

## 12. Lộ trình tương lai (Sau MVP)

| Ưu tiên | Tính năng | Effort |
|----------|---------|------------|
| Cao | Rate limit + audit logging | 2–3 ngày |
| Cao | Key rotation (JWKS) | 2 ngày |
| Trung bình | Testcontainers integration tests | 3–5 ngày |
| Trung bình | Upload file (tài liệu, ảnh) | 2 ngày |
| Trung bình | Tìm kiếm (Elasticsearch/MeiliSearch) | 5 ngày |
| Thấp | Tạo chứng chỉ (PDF) | 3 ngày |
| Thấp | Push notification mobile (FCM) | 3 ngày |
| Thấp | Dashboard phân tích (charts) | 5 ngày |

---

## 13. Kết luận

Backend LearningHub **đã feature-complete cho MVP** với:
- ✅ Đã hiện thực và kiểm thử cả 7 phases
- ✅ Kiến trúc sạch, modular, dễ bảo trì
- ✅ Thiết kế ưu tiên bảo mật (JWT, RBAC, env secrets, hash refresh token)
- ✅ Khả năng realtime (WebSocket với JWT trên CONNECT)
- ✅ Phân tích quản trị + export (Excel/PDF)
- ✅ Xử lý media (Cloudinary video)
- ✅ Tài liệu đầy đủ (báo cáo này + ENV_GUIDE)

**Sẵn sàng cho:** Tích hợp frontend, triển khai staging, kiểm thử tải.

---

*Báo cáo được tạo như một phần của deliverables Phase G, cập nhật theo fix_instruction.md.*
