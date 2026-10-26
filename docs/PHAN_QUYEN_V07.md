# Phân quyền v0.7

- USER: GET /order/{id}, POST /order, GET /my-orders.
- SHIPPER: chỉ trang /shipper và thao tác đơn được phân công; không thể đặt món.
- VENDOR/MANAGER/ADMIN: nghiệp vụ tương ứng; không đặt món bằng tài khoản nhân sự.
- Guest: xem menu, đăng nhập trước khi đặt món.
- Cả GET và POST đặt hàng đều được Spring Security kiểm tra ở backend.
- Giao diện ẩn nút đặt món và liên kết đơn cá nhân đối với tài khoản nhân sự.

Kiểm thử: đăng nhập SHIPPER, mở /order/1 hoặc gửi POST /order phải nhận 403; USER được đặt món.
