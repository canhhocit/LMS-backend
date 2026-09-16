# Tài liệu Đặc tả Kiến trúc GraphQL API - LearningHub System

## 1. Tổng quan & Khái niệm (Overview)

**GraphQL** (do Facebook phát triển) là ngôn ngữ truy vấn dữ liệu cho API (Data Query Language). Khác với REST API truyền thống (nơi Server quy định sẵn các thuộc tính trả về), **GraphQL cho phép Client (Frontend Web / Mobile App) tự do lựa chọn chính xác các trường dữ liệu cần thiết**.

---

## 2. Mục đích Triển khai trong Hệ thống LearningHub

- **Tối ưu ứng dụng Di động (Mobile App)**: Tránh truyền tải thừa dữ liệu qua mạng 3G/4G/5G, tiết kiệm đến 80% dung lượng băng thông di động.
- **Giảm số lượng HTTP Request**: Cho phép Client lấy dữ liệu từ nhiều nguồn (Lịch học + Bảng điểm + Thông báo) chỉ trong **duy nhất 1 HTTP Request**.
- **Linh hoạt cho Frontend**: Đội ngũ phát triển giao diện có thể thêm/bớt thông tin trên UI mà không cần yêu cầu lập trình viên Backend phải sửa mã nguồn Java hay viết thêm API mới.

---

## 3. So sánh REST API vs GraphQL API trong LearningHub

| Tiêu chí | 🌐 REST API (`/api/v1/...`) | ⚡ GraphQL API (`/api/v1/graphql`) |
| :--- | :--- | :--- |
| **Phù hợp cho** | Web Admin Dashboard, Quản lý CRUD tĩnh | Mobile App Sinh viên, Giao diện linh hoạt |
| **Cấu trúc dữ liệu trả về** | Cố định theo DTO của Server | Tùy biến theo câu `query` của Client |
| **Số lượng Request** | Nhiều request nối tiếp (Multiple Round-trips) | Gom tất cả vào 1 Request duy nhất |
| **Băng thông mạng** | Dễ bị dư thừa dữ liệu (Over-fetching) | Tối ưu 100%, chỉ lấy đúng trường cần |

---

## 4. Điểm cuối API & Cấu trúc Truy vấn (API Endpoint & Usage)

- **Endpoint**: `POST /api/v1/graphql`
- **Content-Type**: `application/json`

### Ví dụ 1: Truy vấn danh sách Khóa học (Chỉ lấy Mã, Tên và Tín chỉ)

**Request Payload (JSON)**:
```json
{
  "query": "{ courses { id code title credit } }"
}
```

**Response (JSON)**:
```json
{
  "data": {
    "courses": [
      {
        "id": 1,
        "code": "CS101",
        "title": "Lập trình Java Căn Bản",
        "credit": 3
      },
      {
        "id": 2,
        "code": "CS102",
        "title": "Cấu trúc dữ liệu & Giải thuật",
        "credit": 4
      }
    ]
  }
}
```

---

## 5. Điểm Nổi bật & Giá trị khi Bảo vệ Đồ án Tốt nghiệp

1. **Chuẩn Kiến trúc Enterprise**: Thể hiện khả năng thiết kế hệ thống hiện đại, hỗ trợ song song **RESTful API** cho Web Admin và **GraphQL API** cho Mobile App.
2. **Khả năng Mở rộng (Scalability)**: Dễ dàng tích hợp với các ứng dụng bên thứ ba (Third-party integrations) mà không sợ vỡ hợp đồng API (API contract break).
