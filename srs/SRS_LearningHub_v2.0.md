# TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM

*Software Requirements Specification (SRS)*

─────────────────────────────────────

> ⚠️ DEPRECATED: Tài liệu mô tả hướng "Online Learning / mentor-learner", HIỆN KHÔNG áp dụng. Hệ thống theo mô hình ĐH truyền thống (Clazz-based). Xem srs/SRS_LearningHub_v3.0.md.

- **Phiên bản:** 2.0 (2026‑08‑21)
- **Mục tiêu:** Đưa hệ thống lên kiến trúc **Clean Architecture** để tách rời domain, use‑case và framework, tăng khả năng bảo trì, mở rộng và chuẩn bị cho việc chuyển sang micro‑service trong các giai đoạn sau.

## 2. Phạm vi và Đối tượng
- Đối tượng: BA, Backend Dev, Frontend Dev, QA/Tester, Architect.
- Phạm vi: Toàn bộ các module hiện có (Auth, User, Mentor, Course, Enrollment, Progress, Review, Chat, Admin) + các module dự kiến cho Phase 2 (Notification, Payment mock, Sequential lesson lock).

## 3. Kiến trúc tổng quan (Clean Architecture)

```
src/main/java/com/ex/learninghub
│
├─ common
│   ├─ enums               # CourseStatus, EnrollmentStatus, MentorRequestStatus, NotificationType, PaymentStatus, …
│   ├─ exception           # ErrorCode, AppException, GlobalExceptionHandler
│   └─ response            # ApiResponse<T>
│
├─ modules
│   ├─ auth
│   │   ├─ domain          # User, RefreshToken, Role, UserStatus
│   │   ├─ application     # LoginUseCase, RefreshTokenUseCase, LogoutUseCase
│   │   └─ infrastructure   # AuthController, JwtTokenProvider, SecurityConfig, UserRepository
│   ├─ mentor
│   │   ├─ domain          # MentorRequest, MentorRequestStatus
│   │   ├─ application     # CreateMentorRequestUseCase, ApproveMentorRequestUseCase, RejectMentorRequestUseCase
│   │   └─ infrastructure   # MentorRequestController, MentorRequestMapper, MentorRequestRepository
│   ├─ course
│   │   ├─ domain          # Course, Chapter, Lesson, CourseStatus
│   │   ├─ application     # CreateCourseUC, UpdateCourseUC, PublishCourseUC, DeleteCourseUC, ManageChapterUC, ManageLessonUC
│   │   └─ infrastructure   # CourseController, AdminCourseController, CourseMapper, CourseRepository, ChapterRepository, LessonRepository
│   ├─ enrollment
│   │   ├─ domain          # Enrollment, LessonProgress, EnrollmentStatus
│   │   ├─ application     # EnrollUC, CompleteLessonUC, GetProgressUC
│   │   └─ infrastructure   # EnrollmentController, ProgressController, EnrollmentRepository, LessonProgressRepository
│   ├─ review
│   │   ├─ domain          # Review
│   │   ├─ application     # AddReviewUC, UpdateReviewUC, DeleteReviewUC, GetReviewsUC
│   │   └─ infrastructure   # ReviewController, ReviewMapper, ReviewRepository
│   ├─ chat
│   │   ├─ domain          # Message
│   │   ├─ application     # SendMessageUC, GetMessagesUC, MarkReadUC
│   │   └─ infrastructure   # ChatController, MessageMapper, MessageRepository, WebSocketConfig
│   ├─ admin
│   │   ├─ domain          # DashboardStats, AdminUserManagement
│   │   ├─ application     # GetDashboardUC, ManageUserUC, ApproveMentorUC, ApproveCourseUC, DeleteReviewUC
│   │   └─ infrastructure   # AdminController, AdminService
│   └─ notification (Phase 2)
│       ├─ domain          # Notification, NotificationType
│       ├─ application     # CreateNotificationUC, GetUserNotificationsUC
│       └─ infrastructure   # NotificationController (mock), NotificationRepository
│
└─ configuration
    ├─ BeanConfig            # Wiring use‑cases to adapters
    └─ SwaggerConfig, WebSocketConfig, etc.
```

### Luồng dữ liệu
```
Controller  →  Use‑Case (Application)  →  Domain Entity  →  Repository (Infrastructure)  →  DB
WebSocket   →  Service (Application)   →  Domain Entity  →  Repository
```
Các **Use‑Case** chịu trách nhiệm business logic, không phụ thuộc vào Spring hay JPA. Các **Ports** được mô tả bằng interface trong `application` và được triển khai trong `infrastructure`.

## 4. Phân quyền RBAC
| Role   | Quyền chính |
|--------|--------------|
| **LEARNER** | Đăng ký, đăng nhập, xem/đăng ký khóa học, tiến độ, chat 1‑1, viết review, gửi yêu cầu mentor |
| **MENTOR** | Tạo, sửa, xóa khoá học (chỉ của mình), quản lý chapter/lesson, xem danh sách học viên đã enroll, chat với học viên |
| **ADMIN**  | Quản trị người dùng, duyệt mentor, duyệt/archived khóa học, xem dashboard, xóa review vi phạm |

## 5. Mô tả nghiệp vụ chi tiết (cập nhật)

### 5.1 Auth (hoàn chỉnh)
- `POST /auth/login` – trả JWT.
- `POST /auth/refresh` – cấp lại access token.
- `POST /auth/logout` – thu hồi refresh token (mock, lưu trong DB).

### 5.2 Mentor Request
- `POST /mentor-requests` – tạo yêu cầu (status=PENDING). Kiểm tra không tồn tại yêu cầu PENDING khác.
- `PATCH /mentor-requests/{id}/approve` – ADMIN duyệt, đồng thời cập nhật `users.role = MENTOR`.
- `PATCH /mentor-requests/{id}/reject` – ADMIN từ chối, bắt buộc `rejectionReason`.

### 5.3 Course
- **Lifecycle**: `DRAFT → PENDING_REVIEW → PUBLISHED → ARCHIVED`.
- Các endpoint: CRUD (mentor), `PATCH /courses/{id}/status` (đổi trạng thái), `GET /courses` (filter by status, keyword), `GET /courses/{id}` (chi tiết kèm chapters & lessons).
- Quy tắc: Chỉ `PUBLISHED` mới hiển thị cho learner và cho phép enroll (`COURSE_NOT_PUBLISHED` error nếu không).

### 5.4 Enrollment & Progress
- `POST /enrollments` – tạo enrollment, tự động tạo bản ghi `LessonProgress` cho toàn bộ lesson trong course.
- `POST /progress/lessons/{lessonId}/complete` – learner đánh dấu hoàn thành, tính lại % và chuyển `enrollment.status` sang `COMPLETED` khi 100 %.
- `GET /enrollments/{id}/progress` – trả danh sách lesson + trạng thái.

### 5.5 Review
- `POST /courses/{id}/reviews` – chỉ learner có enrollment COMPLETED được cho review.
- `PUT /reviews/{id}` – sửa review của chính mình.
- `DELETE /reviews/{id}` – người tạo hoặc ADMIN.

### 5.6 Chat
- STOMP endpoint `/app/chat.send` – gửi tin nhắn `{receiverId, courseId, content}`.
- Tin nhắn được **persist** vào bảng `messages` trước khi broadcast.
- `GET /messages?withUserId=&courseId=` – trả lịch sử chat, hỗ trợ pagination.

### 5.7 Admin
- Dashboard: số lượng user, số khóa học PUBLISHED, số enrollments, số mentor request đang chờ.
- `PATCH /users/{id}/status` – khóa/mở khóa user.
- `PATCH /courses/{id}/status` – duyệt hoặc archive khóa học.
- `DELETE /reviews/{id}` – xóa review vi phạm.

### 5.8 Notification (Phase 2 – ngoài MVP)
- API mock `/notifications` trả danh sách thông báo in‑app.
- Các sự kiện tạo thông báo: mentor request duyệt, course publish, tin nhắn mới, review mới.

### 5.9 Payment mock (Phase 2)
- Endpoint `/payments/mock` nhận `orderId`, trả `status: SUCCESS | FAILURE`.

## 6. Các Enum mới
```java
public enum CourseStatus { DRAFT, PENDING_REVIEW, PUBLISHED, ARCHIVED }
public enum EnrollmentStatus { ACTIVE, COMPLETED, CANCELLED }
public enum MentorRequestStatus { PENDING, APPROVED, REJECTED }
public enum NotificationType { MENTOR_REQUEST, COURSE_PUBLISHED, NEW_MESSAGE, NEW_REVIEW }
public enum PaymentStatus { SUCCESS, FAILURE }
```

## 7. Yêu cầu phi chức năng
| Yêu cầu | Mô tả |
|--------|------|
| **Bảo mật** | JWT HS256, BCrypt, CSRF cho WebSocket, refresh‑token revocation. |
| **Hiệu năng** | Cache kết quả khóa học (`@Cacheable`), pagination cho danh sách, tối đa 500 ms cho các endpoint chính. |
| **Testability** | Unit test cho Use‑Case (Mockito), Integration test cho Controller (MockMvc). |
| **Logging** | Structured logging (MDC) + correlation‑id tới mọi request. |
| **Migration** | Flyway cho mọi thay đổi schema, không insert dữ liệu mẫu trong migration. |
| **Documentation** | Swagger/OpenAPI, mẫu request/response trong `docs/V1.0.0_tao_user.md`. |

## 8. Lộ trình phát triển
1. **Phase 1 (MVP – Clean Architecture)**
   - Tái cấu trúc code hiện có thành các layer Domain / Application / Infrastructure.
   - Hoàn thiện các module core (Auth, Mentor, Course, Enrollment, Progress, Review, Chat, Admin).
   - Kiểm thử end‑to‑end: đăng ký → login → xem khóa → enroll → học → chat → review.

2. **Phase 2**
   - Thêm Notification đa kênh (in‑app, email mock).
   - Payment thực (tích hợp gateway).
   - Học tuần tự (lesson lock) – kiểm tra `LESSON_LOCKED` error.

3. **Phase 3**
   - Tách micro‑service (auth‑service, course‑service, learning‑service, chat‑service).
   - Event‑driven (Kafka/RabbitMQ) cho notification, audit.
   - Redis cache, circuit breaker, rate‑limit login.

## 9. Tài liệu kèm
- **API Specification**: các endpoint, phương thức, request/response mẫu (xem `docs/V1.0.0_tao_user.md`).
- **Sequence Diagrams** cho các luồng: login, enroll, complete lesson, review, mentor request.
- **Diagram Clean Architecture** (ở mục 3).

---

*File này là bản SRS_LearningHub_v2.0, cung cấp hướng dẫn chi tiết cho việc nâng cấp hệ thống sang Clean Architecture và các tính năng mở rộng trong các giai đoạn tương lai.*
