# UTEMILKTEA v1.0-dev — Gộp món theo chi nhánh

## Đã thay đổi
- Thêm bảng `order_lines` thông qua JPA entity `OrderLine` để lưu các món, số lượng, tùy chọn và đơn giá trong một đơn.
- Khi checkout giỏ hàng, các món cùng `shop.id` tạo một `CustomerOrder` với nhiều `OrderLine`; món từ các cửa hàng khác nhau tạo đơn riêng.
- Tổng tiền đơn được cộng từ các dòng hàng. Danh sách đơn User/Vendor có mục xem chi tiết các món.
- Đơn cũ không có `order_lines` vẫn hiển thị bằng các trường cũ của `customer_orders`.
- Giữ nguyên luồng đặt một món, Vendor, Shipper và phân quyền v0.9.

## Giới hạn chưa hoàn thành
- Bản đồ, định tuyến, phí giao hàng, thời gian dự kiến.
- Giỏ hàng lưu database, voucher, loyalty, OTP email, WebSocket, thanh toán sandbox.
- CRUD và thống kê đầy đủ Manager/Admin.
- Chưa được build bằng Maven hay kiểm thử trên SQL Server trong môi trường tạo ZIP.

## Cách kiểm thử
1. Sao lưu database UTEMILKTEA_DB trước khi chạy; xem `application.properties` và kiểm tra `spring.jpa.hibernate.ddl-auto=update` để thêm bảng mới.
2. Đăng nhập USER; thêm **hai món cùng một chi nhánh** vào giỏ và checkout. Kỳ vọng **một** mã đơn, có hai dòng chi tiết, tổng tiền bằng tổng hai món.
3. Thêm một món ở chi nhánh thứ hai, checkout. Kỳ vọng một đơn cho mỗi chi nhánh.
4. Đăng nhập Vendor của chi nhánh, xem chi tiết món trong đơn; chuyển trạng thái và phân công Shipper như trước.
5. Kiểm tra đơn cũ trước khi nâng cấp vẫn hiển thị; tài khoản SHIPPER không được truy cập /cart.
6. Kiểm tra lỗi checkout ngoài giờ cửa hàng; không được tạo đơn một phần.

Đây là bản phát triển cần chạy thử, **không phải** bản v1.0 đầy đủ theo toàn bộ đặc tả.
