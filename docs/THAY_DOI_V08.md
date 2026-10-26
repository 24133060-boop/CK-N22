# UTEMILKTEA v0.8 — Bản nâng cấp vận hành

Đã bổ sung: giờ nhận đơn từng chi nhánh (mặc định 08:00–22:00), kiểm tra giờ ở backend, Vendor bật/tắt bán món, ghi lý do/người/thời gian hủy đơn, lọc đơn phía trình duyệt theo mã/trạng thái/ngày, xác nhận trước thao tác quan trọng, trạng thái hoàn tất và danh sách trống rõ ràng, tab Shipper đang thực hiện/lịch sử. Giữ kiểm tra phân quyền Vendor và Shipper phía backend.

**Chưa triển khai**: bản đồ và tính phí vận chuyển thực tế, thanh toán sandbox, OTP email, giỏ hàng đa cửa hàng, dashboard thống kê chuyên sâu, WebSocket, Cloudinary, JWT. Các chức năng này vẫn nằm trong phạm vi yêu cầu dự án nhưng không được mô tả là đã hoàn thành ở v0.8.

**Lưu ý**: `spring.jpa.hibernate.ddl-auto=update` có thể thêm các cột mới; sao lưu database trước khi chạy. Chưa chạy thử trực tiếp với SQL Server/STS trên máy người dùng. Kiểm thử giờ theo múi giờ Asia/Ho_Chi_Minh; các bản ghi Shop cũ sẽ có giờ NULL, backend dùng 08:00–22:00 dự phòng, có thể thiết lập lại giờ từ Vendor.
