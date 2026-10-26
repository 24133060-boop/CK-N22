# Đối chiếu sơ bộ với yêu cầu giảng viên

Đây là rà soát tĩnh mã nguồn; **chưa xác nhận qua kiểm thử tích hợp trên SQL Server**.

## Những gì đã có cấu trúc triển khai
- Spring Boot, Spring Security, OTP đăng ký và đặt lại mật khẩu, tìm kiếm, giỏ hàng, đơn hàng, voucher, thanh toán COD/VietQR, ảnh Cloudinary, các giao diện Guest/User/Vendor/Manager/Admin/Shipper.
- Đã sửa trong bản này: phụ thu size/topping tập trung ở `OptionPricingService`, tính tại backend khi đặt đơn trực tiếp, đặt nhóm và hiển thị giỏ hàng. Giá hiển thị tức thời ở `order.jsp`.

## Các khoảng trống/cần kiểm thử so với đề gốc
- Guest: kiểm tra chính xác số lượng hơn 10 sản phẩm và thứ tự giảm dần.
- User: kiểm tra 20 sản phẩm theo từng tiêu chí, phân trang/lazy loading, sản phẩm đã xem, nhiều địa chỉ, đánh giá tối thiểu 50 ký tự, upload ảnh/video review, quy trình trả hàng/hoàn tiền.
- Vendor: kiểm tra đăng ký shop, chương trình khuyến mãi, báo cáo doanh thu, quyền giới hạn theo shop.
- Manager/Admin: kiểm tra đầy đủ quản lý user, danh mục, chiết khấu app, chương trình khuyến mãi và nhà vận chuyển, phân quyền đúng phạm vi.
- Shipper: kiểm tra thống kê đơn được phân công và bảo vệ dữ liệu đơn của người khác.
- Kiểm tra bảo mật API, CSRF, validation, tính chính xác trạng thái thanh toán; VietQR không tự chứng minh tiền đã vào tài khoản.
- Kiểm tra GitHub commits, kiến trúc 3 tầng, responsive và test trình duyệt thực tế.

## Phạm vi sửa lần này
Bảng giá tùy chọn là mức giá mẫu thống nhất cho các món, chưa có chức năng Vendor tự cấu hình phụ thu. Mỗi món hiện chọn **một** loại topping. Không thay đổi cấu trúc DB. Cần thử lại đơn cũ trong giỏ, đơn mới, voucher, phí ship, VietQR và đơn nhiều chi nhánh.
