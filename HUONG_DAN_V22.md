# UTEMILKTEA v2.2 – OTP khôi phục mật khẩu và giỏ hàng SQL Server

## Đã triển khai
- Giỏ hàng người dùng đăng nhập được lưu thành JSON trong bảng `cart_snapshots` trên SQL Server; khi đăng nhập lại ở phiên mới, dữ liệu được đọc từ database. Thao tác thêm/sửa/xóa đồng bộ xuống DB; thanh toán thành công xóa snapshot.
- Trang `/forgot-password` cho phép yêu cầu mã OTP 6 số qua SMTP; OTP hash BCrypt trong bảng `password_reset_otps`, hạn 10 phút, cách gửi tối thiểu 2 phút, tối đa 5 lần thử, xóa sau sử dụng. Trả thông báo chung khi yêu cầu OTP để giảm lộ thông tin tài khoản.
- Giữ nguyên checkout đa chi nhánh và nghiệp vụ phiên bản v2.1.

## Cấu hình email
Cấu hình các biến môi trường trước khi chạy STS: `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_FROM`, `SMTP_AUTH`, `SMTP_STARTTLS`. Ví dụ Gmail dùng host `smtp.gmail.com`, port `587`, SMTP_USERNAME là email và SMTP_PASSWORD là mật khẩu ứng dụng (không phải mật khẩu đăng nhập thường). Không commit bí mật lên GitHub.

## Kiểm thử cần chạy trên máy của bạn
1. Trong STS: Maven > Update Project, Maven clean, chạy lại ứng dụng.
2. Đăng nhập USER, thêm 2 món, đăng xuất/đóng trình duyệt, đăng nhập lại: `/cart` vẫn có món. Cập nhật/xóa món và kiểm tra sau khi đăng nhập lại.
3. Checkout thành công: giỏ trống; xác nhận bảng `cart_snapshots` đã xóa bản ghi của email.
4. Cấu hình SMTP, thử gửi OTP cho email thật, nhập sai 5 lần, OTP hết hạn, OTP đúng đổi mật khẩu, đăng nhập lại.
5. Kiểm tra lại các vai trò, voucher, đơn hàng, vendor/shipper như bản v2.1.

## Chưa tích hợp
- Xác thực OTP **trước khi tạo tài khoản** (hiện có OTP khôi phục mật khẩu); JWT, WebSocket, Cloudinary, Sitemesh, VNPay/MoMo sandbox chưa hoàn thiện. Các dịch vụ bên ngoài cần cấu hình và kiểm thử riêng. Không được báo cáo là đã xong.
- Chưa có điều kiện chạy Maven và SQL Server thực tế trong môi trường tạo ZIP này. Đây là cập nhật mã nguồn cần bạn kiểm thử.
