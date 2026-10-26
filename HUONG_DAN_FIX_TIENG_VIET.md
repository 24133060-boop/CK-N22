# UTEMILKTEA v0.4 — sửa tiếng Việt

Phiên bản này giữ giao diện v0.3 và thêm cấu hình UTF-8 cùng kiểu Unicode cho dữ liệu.

## Nếu đã chạy bản cũ và thấy dấu `?`

1. Dừng Spring Boot trong STS.
2. Mở SSMS, kết nối SQL Server cổng `14330` và sao lưu dữ liệu nếu cần.
3. Mở `database/02-fix-vietnamese.sql` và chạy bằng **Execute**. File dùng UTF-8 có BOM; các chuỗi Unicode trong SQL có tiền tố `N`.
4. Import bản v0.4 bằng **Existing Maven Projects** (hoặc thay source cũ).
5. **Maven > Update Project**, rồi chạy lại Spring Boot.
6. Mở `http://localhost:8080` và nhấn Ctrl+F5.

Nếu lỗi ALTER TABLE do ràng buộc khác, dừng và gửi thông báo SQL Server để xử lý, không xóa database.

## Nguyên nhân

Trang JSP đã khai báo UTF-8 nhưng dữ liệu cũ trong SQL Server đã lưu sai mã hóa (ký tự bị thay bằng `?`). Đổi CSS hoặc charset của trang không phục hồi được dữ liệu; cần cập nhật dữ liệu cũ và lưu Unicode (`NVARCHAR`) về sau.

**Giới hạn:** Chưa thể kiểm thử trên SQL Server thực tế của bạn. Script sửa dữ liệu mẫu mặc định, không sửa tùy ý dữ liệu người dùng.
