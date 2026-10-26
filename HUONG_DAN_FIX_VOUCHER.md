# UTEMILKTEA v2.0 — Bản sửa lỗi SQL Server Voucher

## Lỗi đã xử lý
- `Incorrect syntax near the keyword 'percent'` khi vào `/manager/vouchers`.
- Entity `Voucher.percent` được map sang cột `discount_percent` (tên thuộc tính Java và form vẫn là `percent`).
- Kiểm tra giá trị voucher không hợp lệ và ngày hết hạn ở quá khứ.

## Chạy trên SQL Server đã dùng các phiên bản trước
1. **Sao lưu database `UTEMILKTEA_DB`.**
2. Dừng Spring Boot.
3. Mở SSMS, chọn database `UTEMILKTEA_DB`, chạy `database/04-fix-voucher-percent.sql` **một lần**. Script sẽ đổi tên cột `[percent]` sang `discount_percent` nếu cần; không xóa voucher.
4. Import project vào STS, Maven > Update Project, Project > Clean, chạy Spring Boot.
5. Vào `/manager/vouchers`, tạo `WELCOME10`, kiểm tra áp dụng ở giỏ hàng (PICKUP) và chi tiết đơn.

Nếu database chưa có bảng `vouchers`, không cần chạy script; Hibernate với `ddl-auto=update` sẽ tạo bảng mới.

## Lưu ý
- Chưa thể chạy integration test với SQL Server trên máy người dùng trong môi trường đóng gói.
- Không sửa thông tin đăng nhập database, không xóa đơn hàng hay dữ liệu hiện tại.
- Voucher áp dụng riêng từng đơn theo chi nhánh; nếu có đơn không đủ mức tối thiểu, checkout sẽ báo lỗi và rollback.
