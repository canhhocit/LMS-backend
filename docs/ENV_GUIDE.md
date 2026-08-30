# Hướng dẫn cấu hình biến môi trường LearningHub

## Tổng quan

Tài liệu này hướng dẫn cách cấu hình tất cả biến môi trường cần thiết để chạy backend LearningHub. Toàn bộ cấu hình nhạy cảm được tách ra biến môi trường — **tuyệt đối không hardcode secret trong source code**.

---

## Khởi động nhanh

```bash
# 1. Sao chép file mẫu
cp .env.example .env

# 2. Chỉnh sửa với giá trị của bạn
nano .env   # hoặc editor bất kỳ

# 3. Nạp biến môi trường (chọn 1 cách):
# Cách A: Export thủ công trước khi chạy
export $(cat .env | xargs) && ./mvnw spring-boot:run

# Cách B: Dùng direnv (tự động nạp khi cd vào thư mục)
direnv allow

# Cách C: Cấu hình trong IDE run configuration
```

---

## Biến bắt buộc

| Biến | Mô tả | Bắt buộc | Giá trị mặc định | Ví dụ |
|----------|-------------|----------|---------|---------|
| `DB_URL` | Chuỗi kết nối JDBC tới PostgreSQL | ✅ | `jdbc:postgresql://localhost:5432/learninghub` | `jdbc:postgresql://db:5432/learninghub` |
| `DB_USERNAME` | Tên đăng nhập database | ✅ | `postgres` | `learninghub_user` |
| `DB_PASSWORD` | Mật khẩu database | ✅ | `postgres` | `MatKhauMan_2026!` |
| `JWT_SECRET` | Khóa ký HS256 (≥32 ký tự) | ✅ | (giá trị dự phòng hardcoded) | `openssl rand -base64 32` |
| `MAIL_USERNAME` | Tài khoản SMTP Brevo | ✅ | — | `user@domain.com` |
| `MAIL_PASSWORD` | Mật khẩu SMTP Brevo | ✅ | — | `xkeysib-xxxxxxxxx` |
| `CLOUDINARY_CLOUD_NAME` | Tên cloud Cloudinary | ✅ | — | `my-cloud` |
| `CLOUDINARY_API_KEY` | API key Cloudinary | ✅ | — | `123456789012345` |
| `CLOUDINARY_API_SECRET` | API secret Cloudinary | ✅ | — | `abcdefghijklmnopqrstuvwxyz` |

---

## Biến tùy chọn (có giá trị mặc định)

| Biến | Mô tả | Giá trị mặc định |
|----------|-------------|---------|
| `JWT_EXPIRATION` | Thời gian hết hạn access token (ms) | `86400000` (24 giờ) |
| `FRONTEND_URL` | URL frontend dùng cho link trong email | `http://localhost:3000` |
| `CORS_ALLOWED_ORIGINS` | Danh sách origin được phép, phân cách bằng dấu phẩy | `http://localhost:3000` |
| `ATTENDANCE_MAX_ABSENT_RATIO` | Tỷ lệ vắng tối đa (0.0–1.0) | `0.2` (tức 20%) |
| `SERVER_PORT` | Cổng HTTP (cấu hình qua `server.port`) | `8080` |

---

## Cách lấy thông tin xác thực

### 1. PostgreSQL Database

**Môi trường dev:**
```bash
# Dùng Docker
docker run -d \
  --name learninghub-postgres \
  -e POSTGRES_DB=learninghub \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15
```
Sau đó cấu hình:
```
DB_URL=jdbc:postgresql://localhost:5432/learninghub
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

**Production:** Nên dùng managed DB (AWS RDS, Azure Database, GCP Cloud SQL) — cập nhật `DB_URL` theo endpoint được cấp.

### 2. JWT Secret

Sinh khóa bảo mật:
```bash
# Linux/macOS/Git Bash
openssl rand -base64 32

# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```
Khóa phải **≥ 32 ký tự** (256 bits) cho thuật toán HS256.

### 3. Brevo SMTP (Email)

1. Tạo tài khoản tại [brevo.com](https://brevo.com)
2. Vào **SMTP & API** → **SMTP** → **SMTP Settings**
3. Sao chép:
   - `MAIL_USERNAME` = SMTP login (VD: `user@domain.com`)
   - `MAIL_PASSWORD` = SMTP key (VD: `xkeysib-xxxxxxxxxxxxx`)

Host (`smtp-relay.brevo.com`) và port (`587`) đã được cấu hình sẵn trong `application.yml`.

### 4. Cloudinary (Upload video)

1. Đăng ký tại [cloudinary.com](https://cloudinary.com)
2. Vào Dashboard → **Account Details** → sao chép:
   - `CLOUDINARY_CLOUD_NAME`
   - `CLOUDINARY_API_KEY`
   - `CLOUDINARY_API_SECRET`

Giới hạn upload video được cấu hình trong `application.yml`:
```yaml
app:
  upload:
    max-video-size: 200MB
spring:
  servlet:
    multipart:
      max-file-size: 200MB
      max-request-size: 200MB
```

---

## Tra cứu biến theo module

| Module | Biến sử dụng |
|--------|----------------|
| **Auth** (JWT, Quên mật khẩu) | `JWT_SECRET`, `JWT_EXPIRATION`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `FRONTEND_URL` |
| **Database / Flyway** | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| **Email (Brevo)** | `MAIL_USERNAME`, `MAIL_PASSWORD` |
| **Upload Video** | `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` |
| **CORS / WebSocket** | `CORS_ALLOWED_ORIGINS`, `FRONTEND_URL` |
| **Điểm danh** | `ATTENDANCE_MAX_ABSENT_RATIO` |

---

## Checklist kiểm tra

Trước khi khởi động ứng dụng, xác nhận:

- [ ] File `.env` đã tồn tại ở thư mục gốc
- [ ] Tất cả biến **bắt buộc** đã được đặt (không còn giá trị mẫu)
- [ ] `JWT_SECRET` dài ≥ 32 ký tự
- [ ] MySQL đang chạy và truy cập được tại `DB_URL`
- [ ] Thông tin Brevo SMTP hoạt động (test bằng `telnet smtp-relay.brevo.com 587`)
- [ ] Thông tin Cloudinary hợp lệ (test qua dashboard)
- [ ] `FRONTEND_URL` khớp với deployment frontend của bạn

---

## Xử lý lỗi thường gặp

| Lỗi | Nguyên nhân | Cách xử lý |
|-------|-------|-----|
| `Communications link failure` | Không kết nối được MySQL | Kiểm tra `DB_URL`, firewall, MySQL có chạy không |
| `Bad credentials` (DB) | Sai user/pass | Kiểm tra `DB_USERNAME`, `DB_PASSWORD` |
| `JWT signature does not match` | Khóa bí mật không khớp | Đảm bảo `JWT_SECRET` giống nhau qua các lần khởi động |
| `403 Forbidden` khi gửi email | Sai thông tin Brevo | Kiểm tra lại `MAIL_USERNAME`/`MAIL_PASSWORD` |
| `Upload failed` (video) | Lỗi xác thực Cloudinary | Kiểm tra đủ 3 biến Cloudinary |
| `CORS error` trên browser | Origin chưa được phép | Thêm URL frontend vào `CORS_ALLOWED_ORIGINS` |

---

## Thực hành bảo mật tốt

1. **Không commit `.env`** — file này đã có trong `.gitignore`
2. **Dùng giá trị khác nhau cho mỗi môi trường** (dev/staging/prod)
3. **Xoay vòng secret định kỳ** (JWT secret, mật khẩu DB, API keys)
4. **Dùng secret manager ở production** (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault)
5. **Giới hạn CORS origins** — chỉ cho phép frontend đã biết
6. **Dùng mật khẩu DB mạnh** và tài khoản DB riêng (không dùng `root` ở production)
