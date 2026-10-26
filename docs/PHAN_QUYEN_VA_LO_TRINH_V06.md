# UTEMILKTEA v0.6 — bản nâng cấp từng phần

## Đã thực hiện
- Navbar theo trạng thái đăng nhập, avatar dropdown, trang hồ sơ.
- Ghi rõ chi nhánh ở menu và trang đặt món.
- Vendor chỉ xem và xử lý đơn của cửa hàng gán với email Vendor; không thể tự xác nhận giao hàng.
- Vendor xác nhận, chuẩn bị, đánh dấu sẵn sàng; phân công Shipper cho đơn giao hàng.
- Shipper chỉ thấy đơn được phân công, xác nhận lấy hàng và giao thành công.
- Đơn nhận tại quầy được Vendor xác nhận bàn giao, không qua Shipper.
- Thêm đường quay lại cơ bản.

## Chưa thực hiện trong v0.6
- Bản đồ, định tuyến, tính phí giao hàng theo km, ETA.
- JWT, OTP email, WebSocket, Cloudinary, SiteMesh, VNPay/MoMo.
- Giỏ hàng đa cửa hàng, phân quyền Manager theo chi nhánh, CRUD Admin/Manager, voucher, tích điểm, báo cáo.
- Kiểm thử tích hợp với SQL Server của người dùng.

## Lưu ý
- Các cửa hàng mẫu chưa có chủ sở hữu sẽ được gán cho vendor@utemilktea.local khi khởi động, **chỉ phù hợp dữ liệu demo**. Cần thay bằng luồng tạo/duyệt cửa hàng và phân quyền thực tế trước khi triển khai.
- Đường dẫn quay lại dựa trên lịch sử trình duyệt là bản tạm; breadcrumb theo route cần hoàn thiện.
- Database hiện tại được Hibernate tự cập nhật cột `vendor_email`, `shipper_email`, `picked_up_at`, `delivered_at` khi chạy (ddl-auto=update). Hãy sao lưu DB trước.
- Không ghi mật khẩu SQL Server hay API key vào GitHub.
