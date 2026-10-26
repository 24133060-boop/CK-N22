# Kiểm thử nghiệp vụ mới – UTEMILKTEA

## Lưu ý nâng cấp
- Sao lưu SQL Server trước khi chạy. `spring.jpa.hibernate.ddl-auto=update` sẽ thêm cột và bảng mới.
- Không ghi API Secret hay mật khẩu vào GitHub. Kiểm tra biến môi trường STS trước khi chạy.
- MANAGER ban đầu có thể nhìn thấy 0 chi nhánh. **ADMIN phải phân công trước**.
- Thao tác xác nhận VietQR là xác nhận thủ công sau đối soát ngân hàng; không phải API báo có tự động.
- Đề nghị xem đây là bản ứng viên nghiệm thu đến khi Maven, SQL Server và các luồng thực tế vượt qua kiểm thử.

## Kiểm tra theo vai trò
1. ADMIN `/manager/catalog`: chọn chi nhánh, sửa tên/địa chỉ; phân công MANAGER; đặt chiết khấu 0–40%; chọn nhà vận chuyển đã bật ở `/admin/settings`.
2. MANAGER đăng nhập lại → `/manager/catalog`: chỉ thấy chi nhánh được phân công, xem sản phẩm, sửa giá, sửa danh mục, bật/tắt bán, thêm sản phẩm; đổi tên danh mục; tạo/bật tắt khuyến mãi; xem khách đặt hàng tại chi nhánh.
3. MANAGER dùng đường dẫn thủ công đến chi nhánh khác: phải nhận HTTP 403 ở endpoint cập nhật; thống kê `/manager` chỉ hiển thị chi nhánh được phân công.
4. MANAGER thử xác nhận VietQR của đơn chi nhánh khác: phải nhận HTTP 403.
5. MANAGER chặn khách từng mua tại chi nhánh; USER đó không đặt được ở chi nhánh bị chặn, nhưng vẫn được đặt ở chi nhánh khác. Bỏ chặn và thử lại.
6. VENDOR `/vendor/shop/{id}/promotions`: tạo khuyến mãi sản phẩm 10%, hoạt động còn hạn. USER đặt một món qua giỏ: tổng hàng hóa giảm đúng 10%. Chọn voucher: voucher tính trên số dư sau giảm giá cửa hàng.
7. VENDOR tạo khuyến mãi **phí vận chuyển** 20%. USER giao hàng có giá ship >0: phí ship giảm 20%; lấy tại quán không áp dụng phí ship.
8. ADMIN gán một nhà vận chuyển cho shop và thử giao hàng: phí vận chuyển dùng phí cơ bản + phí mỗi km đã cấu hình, thay vì công thức mặc định.
9. USER xem `/my-orders/{id}/detail`: khuyến mãi hàng hóa, phí vận chuyển và tổng giảm phải khớp tổng thanh toán. Kiểm tra cả VietQR.
10. Kiểm tra hồi quy: OTP, đăng nhập, sản phẩm 12 món ở trang chủ, ảnh Cloudinary, tìm kiếm/lọc 20 món/trang, size/topping, COD, VietQR, đánh giá ảnh, trạng thái VENDOR không nhảy lên đầu, SHIPPER, quản lý trả hàng.

## Giới hạn cần biết
- Không tích hợp API hoàn tiền ngân hàng: trả hàng được ghi nhận và xử lý nội bộ, hoàn tiền ngoài đời phải làm thủ công.
- Không có API giao vận thực tế: đơn vị vận chuyển ở đây là cấu hình phí của hệ thống, không phải tạo vận đơn với bên thứ ba.
- Chiết khấu là cấu hình từng shop; chưa có hệ thống quyết toán chiết khấu/tự động thanh toán cho shop.
- Chưa thể chứng nhận chạy thành công nếu chưa biên dịch Maven và thử với SQL Server thực tế.
- Yêu cầu quy trình commit GitHub, báo cáo và trình diễn nhóm cần người nộp hoàn tất riêng.
