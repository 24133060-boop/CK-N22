# Kiểm thử chỉnh sửa giao diện và tải tệp đánh giá

1. Trang chủ: cuộn xuống **Menu nổi bật**; nút **Tìm kiếm & lọc sản phẩm** nằm bên phải tiêu đề trên màn hình rộng, xuống dưới khi màn hình hẹp. Nút trên đầu trang đã bỏ.
2. Mở một sản phẩm **đã nhận hàng thành công**, nhập nhận xét ít nhất 50 ký tự; dùng JPG/PNG/WebP thật dưới 5 MB trước (có thể kiểm tra JPG khoảng 100 KB).
3. Nếu tải lỗi, trang hiển thị rõ lỗi kiểm tra định dạng/cấu hình. Lỗi kết nối Cloudinary được ghi trong Console STS: tìm `Cloudinary review media upload failed`. Gửi dòng lỗi và nguyên nhân chính, **xóa API secret/token** trước khi chia sẻ.
4. Video chỉ hỗ trợ MP4, giới hạn ứng dụng 15 MB; reverse proxy/tường lửa có thể chặn request **trước khi** vào ứng dụng (413, WAF). Nếu gặp trang chặn của proxy, cần giảm tệp hoặc cấu hình giới hạn trên proxy; sửa Java không bỏ qua được tầng chặn này.
5. Bản này không sửa dữ liệu SQL Server, mã Cloudinary đang dùng cho sản phẩm, hoặc logic tạo đơn.

## Ghi chú
Chưa chạy kiểm thử tích hợp với SQL Server và Cloudinary tài khoản của bạn tại môi trường này.
