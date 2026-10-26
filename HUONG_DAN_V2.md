# UTEMILKTEA v2.0 - Bản mở rộng nghiệp vụ (đợt 1)

Nâng cấp từ v1.3 đã được người dùng kiểm thử.

## Đã bổ sung
- USER hủy đơn NEW với lý do; lưu người hủy và thời gian.
- VENDOR chỉnh sửa tên, danh mục, giá sản phẩm thuộc chi nhánh được giao.
- ADMIN cập nhật vai trò tài khoản (không tự đổi vai trò).
- MANAGER/ADMIN xem thống kê số lượng chi nhánh, sản phẩm và đơn hàng.

## Chưa hoàn thành
Voucher, yêu thích, đánh giá, báo cáo doanh thu nâng cao, quản lý đầy đủ chi nhánh và danh mục. Không tuyên bố những phần này đã triển khai.

## Chạy thử
1. Sao lưu database.
2. Import Maven project vào STS, Maven Update Project và chạy Spring Boot.
3. Kiểm tra /my-orders, /vendor/orders/{id}, /admin, /manager với tài khoản đúng vai trò.
4. Kiểm thử phân quyền: USER không được gọi URL vendor/admin; Vendor không được sửa món của cửa hàng khác.

Lưu ý: Thay đổi role yêu cầu đăng nhập lại. Các chỉ số dashboard là tổng số bản ghi, không phải doanh thu.
