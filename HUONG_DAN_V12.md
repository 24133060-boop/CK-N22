# UTEMILKTEA v1.2 - Bổ sung chức năng demo

## Chức năng mới
1. Tìm địa chỉ giao hàng bằng tên đường/số nhà: bấm **Tìm địa chỉ**, chọn một trong tối đa 5 kết quả; bản đồ tự di chuyển và tự tính phí. Có thể nhấn bản đồ để chỉnh lại ghim. Không sử dụng GPS.
2. Đổi nền bản đồ sang OpenStreetMap HOT (máy chủ đã thử truy cập thành công).
3. Cập nhật số lượng từng dòng sản phẩm ngay trong giỏ hàng.
4. Lọc lịch sử đơn hàng theo trạng thái và tìm theo mã đơn/chi nhánh; trạng thái dịch sang tiếng Việt.
5. Đơn hàng cũ thiếu dữ liệu giao hàng hiển thị thông báo thay vì giá trị rỗng.

## Kiểm thử
- Sao lưu database, import Maven project trong STS, Maven Update, chạy Spring Boot.
- Giỏ hàng: sửa số lượng -> Cập nhật -> kiểm tra tổng tiền món.
- Nhập '1 Võ Văn Ngân, Thủ Đức, TP.HCM' -> Tìm địa chỉ -> chọn kết quả -> xem ghim, tuyến đường và phí ship.
- Nếu dịch vụ Nominatim hoặc OSRM công cộng không hoạt động, dùng chọn điểm thủ công hoặc nhận tại cửa hàng. Không liên tục gửi tìm kiếm theo từng ký tự.
- Đơn hàng: lọc trạng thái, tìm mã đơn; xác minh dữ liệu cũ vẫn hiển thị.

## Giới hạn
- Chưa tích hợp voucher, thanh toán online, OTP email, đánh giá, báo cáo doanh thu. Các chức năng đó cần triển khai backend và kiểm thử riêng.
- Phí vận chuyển được tính lại tại backend khi checkout, không tin vào phí từ trình duyệt.
- Chưa chạy Maven compile trong môi trường tạo gói; cần xác nhận chạy trên STS.
