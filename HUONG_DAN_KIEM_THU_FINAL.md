# UTEMILKTEA — BẢN ỨNG VIÊN FINAL (cần chạy nghiệm thu)

## Nguồn và phạm vi
- Kế thừa nguyên trạng ZIP `UTEMILKTEA_SUA_GIA_SIZE_TOPPING_VA_RASOAT.zip` người dùng đã xác nhận test.
- Bổ sung ràng buộc bình luận sản phẩm từ 50 đến 500 ký tự ở cả server và form (theo đề giảng viên).
- Giữ nguyên cấu hình VietQR, Cloudinary, OTP, giá size/topping và database hiện tại.
- Đây là bản ứng viên nộp, **chưa xác minh build và chạy tích hợp SQL Server trong môi trường tạo ZIP**.

## Nghiệm thu trong STS
1. Sao lưu DB và bản project đang chạy. Giải nén ZIP vào thư mục mới. Import Existing Maven Projects. Maven > Update Project; Project > Clean.
2. Run As > Maven test (hoặc `mvn test`); xử lý lỗi nếu có trước khi chạy.
3. Run As > Spring Boot App. Test USER: tìm kiếm, sản phẩm, size S/M/L, topping, thêm giỏ, voucher, COD, VietQR; kiểm tra giá ở mỗi bước.
4. Test đánh giá sản phẩm đã giao: dưới 50 ký tự bị chặn; từ 50 ký tự được chấp nhận; chưa mua không được đánh giá.
5. Test VENDOR: ảnh Cloudinary, sản phẩm, đơn hàng, chuyển trạng thái và phân shipper.
6. Test SHIPPER: chỉ thấy đơn của mình, giao thành công. Test MANAGER và ADMIN theo phạm vi phân quyền.
7. Test quyền truy cập bằng tài khoản khác; kiểm tra responsive; ghi hình và lưu GitHub commits.

## Đối chiếu đề còn chưa chứng minh đầy đủ
- Guest: hơn 10 sản phẩm và thứ tự giảm dần.
- User: các danh sách 20 sản phẩm và phân trang, sản phẩm đã xem, nhiều địa chỉ, review ảnh/video, trả hàng/hoàn tiền.
- Vendor: đăng ký shop, tự quản lý chương trình khuyến mãi.
- Manager/Admin: danh mục, chiết khấu, hãng vận chuyển, quản lý theo phạm vi.
- Shipper: thống kê chi tiết đơn phân công.
- VietQR hiện tạo QR; không tự xác nhận ngân hàng đã nhận tiền nếu thiếu tích hợp đối soát.

Không nên báo cáo đã đạt toàn bộ rubric nếu chưa triển khai và test các mục này.
