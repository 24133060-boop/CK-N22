# UTEMILKTEA – hướng dẫn bản tích hợp

## Môi trường
Java 17, Maven, SQL Server localhost:14330, database UTEMILKTEA_DB, STS. Maven Update Project, Maven clean, Maven test, Spring Boot Run. Backup SQL Server trước khi nâng cấp. Chạy `database/04-fix-voucher-percent.sql` nếu đang nâng từ phiên bản cũ.

## Cấu hình dịch vụ thực
- Email: `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_FROM`, `SMTP_AUTH`, `SMTP_STARTTLS`. Với Gmail dùng App Password.
- Cloudinary: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`.
- JWT: `JWT_SECRET` (chuỗi bí mật dài, riêng cho môi trường chạy).
- VNPay sandbox: `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET`, `VNPAY_RETURN_URL`; đăng ký merchant sandbox tại VNPay để có khóa hợp lệ. Không commit bí mật.
- VNPay IPN: cấu hình URL công khai HTTPS `https://<host>/payment/vnpay/ipn` tại phía VNPay; localhost không nhận IPN từ Internet. Chỉ IPN có chữ ký hợp lệ mới ghi PAID. Return URL không ghi PAID.

## Thanh toán
Tạo đơn hàng như trước, vào Chi tiết đơn, chọn Thanh toán VNPay Sandbox. COD vẫn giữ nguyên. Thanh toán trực tuyến là luồng bổ sung sau khi tạo đơn; trạng thái thanh toán độc lập trạng thái xử lý đơn.

## Giới hạn kiểm thử
Mã nguồn đã được rà soát và ZIP đã được kiểm tra, nhưng chưa được biên dịch Maven hoặc kiểm thử tích hợp SQL Server/SMTP/Cloudinary/VNPay thực tế trong môi trường tạo gói. SiteMesh (phụ thuộc phiên bản Jakarta Servlet) và MoMo chưa được triển khai. Không được xem bản này là đã xác nhận đạt mọi mục của rubric nếu chưa đối chiếu đề bài và chạy test.
