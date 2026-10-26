# API JWT và MapStruct

Đăng nhập JSP vẫn sử dụng Spring Security Session và CSRF bình thường. API `/api/**` sử dụng JWT Bearer (có thể dùng session khi đang đăng nhập).

## Cấu hình

Đặt biến môi trường `JWT_SECRET` bằng chuỗi ngẫu nhiên tối thiểu 32 byte. Không đưa khóa thật vào Git.

## API

`POST /api/auth/login` với JSON `{ "email": "...", "password": "..." }` trả về JWT có hiệu lực 1 giờ. Sai thông tin trả 401; thiếu cấu hình trả 503.

`GET /api/me` với header `Authorization: Bearer <token>` trả thông tin tài khoản đã được MapStruct ánh xạ sang DTO, không tiết lộ mật khẩu.

**Lưu ý:** Không thể xác nhận biên dịch trong môi trường hiện tại vì không có Maven. Cần chạy `mvn clean test` trước khi nghiệm thu.
