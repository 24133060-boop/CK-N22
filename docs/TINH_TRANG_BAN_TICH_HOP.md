# UTEMILKTEA - tình trạng bản tích hợp

Bản này phát triển từ v2.3 và bổ sung **xác thực email OTP khi đăng ký**.

## Đã có trong mã nguồn
- Spring Boot 3.3.6, JSP, SQL Server, Spring Security 5 vai trò.
- Đơn hàng, giỏ hàng lưu DB, voucher, đánh giá, yêu thích, bản đồ, shipper, doanh thu.
- Quên mật khẩu OTP email, xác thực email OTP đăng ký.
- Cloudinary cho ảnh sản phẩm (cần biến môi trường), WebSocket cập nhật đơn hàng.

## Chưa thể xác nhận hoàn thành
- JWT: dự án vẫn dùng Spring Security session cho giao diện JSP; không nên thêm JWT nếu không có API thực sự dùng token.
- SiteMesh, MapStruct: chưa tích hợp.
- VNPay/MoMo sandbox: chưa tích hợp và cần tài khoản sandbox, thông tin merchant hợp lệ.
- Chưa chạy được Maven build/test hay kiểm thử SQL Server tại môi trường tạo file.

## OTP đăng ký
1. Điền /register; hệ thống gửi mã thật qua SMTP cấu hình trong application.properties.
2. Nhập email và mã tại /verify-registration. Tài khoản chỉ được tạo sau khi mã hợp lệ.
3. Mã hết hạn sau 10 phút, tối đa 5 lần nhập sai, hạn chế gửi lại trong 2 phút.
4. Nếu chưa cấu hình SMTP, đăng ký không hoàn tất. Không có mã OTP demo cố định.

## Cấu hình cần có
DB_PORT, DB_USER, DB_PASSWORD; SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD, SMTP_FROM; CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET.

## Trước khi nộp
- Sao lưu database trước khi mở phiên bản mới (Hibernate ddl-auto=update).
- Chạy Maven Update Project và Maven test, khởi động Spring Boot.
- Kiểm tra đăng ký OTP thật, quên mật khẩu, giỏ hàng, checkout, voucher, shipper, phân quyền, upload ảnh, WebSocket.
- Chỉ kết luận đáp ứng rubric sau khi so sánh với đề gốc và chạy thử toàn bộ luồng.
