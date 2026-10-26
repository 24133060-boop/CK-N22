# UTEMILKTEA - VietQR Vietcombank

- Checkout: chọn COD hoặc VietQR. Giỏ hàng nhiều chi nhánh tạo một đơn riêng cho từng chi nhánh; mỗi đơn có QR và số tiền riêng.
- USER: `/my-orders` → Chi tiết đơn → Xem mã VietQR.
- VENDOR: `/vendor/orders/{shopId}` → xem đơn VietQR → kiểm tra sao kê → Xác nhận đã nhận tiền.
- MANAGER: `/manager` → Đối soát chuyển khoản VietQR → xác nhận.
- Cấu hình mặc định: Vietcombank `970436`, tài khoản và chủ tài khoản theo thông tin chủ dự án cung cấp. Có thể ghi đè qua `VIETQR_ACCOUNT_NO` và `VIETQR_ACCOUNT_NAME`.
- QR tạo bằng `https://img.vietqr.io/image/` (cần Internet). Khách chuyển tiền THẬT nếu thực hiện giao dịch.
- Không có callback ngân hàng; KHÔNG tự động đánh dấu PAID. Đối chiếu sao kê thủ công là bắt buộc.
- Mã nội dung `UTEMILKTEA DH{id}`. Không dùng lại QR của đơn khác.
- Chạy STS: Maven Update → Spring Boot App. Giữ DB cũ; Hibernate ddl-auto=update.
- Test: COD vẫn chạy; VietQR đặt đơn → xem QR, đúng số tiền, xác nhận qua Vendor/Manager → PAID; USER không được gọi endpoint xác nhận.
- Hạn chế: không có đối soát tự động, không xử lý hoàn tiền tự động. Không dùng để thanh toán thật trong demo nếu không chủ đích.
