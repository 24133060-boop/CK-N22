# Sửa ảnh Cloudinary trên giao diện USER

- `home.jsp` và `shop.jsp`: thay ảnh emoji cố định bằng `p.imageUrl` từ SQL Server.
- Nếu sản phẩm chưa có URL hoặc URL lỗi, hiện hình trà sữa mặc định.
- `style.css`: ảnh rộng 100%, cao 160px, object-fit cover.
- Không cần thay đổi database, tài khoản Cloudinary hoặc application.properties.

## Cách chạy
1. Sao lưu dự án cũ và giải nén ZIP mới.
2. Import hoặc thay dự án đang chạy trong STS; đảm bảo chỉ một tiến trình dùng cổng 8080.
3. Maven > Update Project, Project > Clean, khởi động lại Spring Boot.
4. Mở trang chủ và `/shop/1`, tải lại Ctrl+Shift+R.
5. Kiểm tra ảnh sản phẩm ID=1 hiện đúng ảnh mới.

Chưa có kiểm thử chạy thực tế với SQL Server/Cloudinary trong môi trường này.
