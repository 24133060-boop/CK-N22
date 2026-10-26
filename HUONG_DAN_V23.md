# UTEMILKTEA v2.3 – Các phần tích hợp tiếp theo

## Bổ sung mã nguồn
- Cloudinary: Vendor có thể tải ảnh JPEG/PNG/WebP tối đa 5 MB, hệ thống lưu URL HTTPS vào Product.imageUrl. Cần CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET thật để sử dụng.
- WebSocket/STOMP: trang Vendor nhận thông báo tổng quát khi có thay đổi đơn hàng. Không gửi thông tin khách hàng trên kênh chung. Nhấn tải lại trang để xem đơn.
- Giữ các tính năng v2.2: OTP quên mật khẩu qua SMTP, giỏ hàng database, voucher, vai trò.

## Cấu hình
Thiết lập DB_USER, DB_PASSWORD, SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD, SMTP_FROM và CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET qua biến môi trường trước khi chạy. Không đưa mật khẩu thật lên GitHub.

## Chưa hoàn tất, KHÔNG coi là đã chạy được
- Xác thực email lúc đăng ký (khác với OTP quên mật khẩu) chưa triển khai.
- JWT chưa triển khai; ứng dụng web JSP hiện dùng Spring Security session/cookie.
- VNPay/MoMo sandbox chưa tích hợp; COD hiện là luồng thanh toán sẵn có.
- SiteMesh và MapStruct chưa tích hợp.
- WebSocket chưa được kiểm thử với máy chủ thực tế.
- Chưa thể biên dịch Maven hoặc kết nối SQL Server từ môi trường đóng gói.

## Kiểm thử sau khi hoàn thiện
1. Import Maven, Update Project, chạy `mvn clean test` hoặc Run As Spring Boot App.
2. Vendor tải ảnh thử, kiểm tra Cloudinary và URL trong database.
3. Mở 2 phiên đăng nhập, tạo đơn và kiểm tra cảnh báo thay đổi trên trang Vendor.
4. Test lại giỏ hàng, voucher, quên mật khẩu, phân quyền và quy trình giao hàng.
