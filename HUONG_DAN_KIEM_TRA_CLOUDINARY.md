# Kiểm tra upload Cloudinary

1. Không đưa API Secret vào application.properties hoặc ảnh chụp màn hình. Nếu secret từng bị lộ, thu hồi key cũ và cấp key mới.
2. STS > Run Configurations > Spring Boot App > utemilktea > Environment: đặt CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET. Apply, dừng ứng dụng cũ, Run lại.
3. Vendor > Cửa hàng của tôi > tải ảnh JPG/PNG dưới 5MB.
4. Nếu thất bại, xem STS Console dòng `Cloudinary product image upload failed` và nguyên nhân (401/403, Unauthorized, Invalid Signature, Forbidden...). Che toàn bộ key/secret/token trước khi gửi log.
5. Nếu là 403/permission denied, kiểm tra quyền của API key UTEMILKTEA ở Cloudinary; Media Library User có thể bị giới hạn quyền theo thư mục. Nếu là 401/Invalid Signature, kiểm tra API Secret thuộc đúng API Key.

Lưu ý: Bản vá này tăng khả năng chẩn đoán và chuẩn hóa khoảng trắng cấu hình; chưa thể xác nhận Cloudinary upload thành công khi không có kết nối tài khoản thực tế.
