# UTEMILKTEA – bản mở rộng chức năng, cần nghiệm thu tại máy người dùng

## Quan trọng
- Không khẳng định bản này đạt 100% yêu cầu gốc. Chưa biên dịch Maven / chạy SQL Server trong môi trường đóng gói. Phải test tại STS và đối chiếu đề bài.
- **Sao lưu database** trước khi chạy. Hibernate `ddl-auto=update` sẽ tạo các bảng mới khi chạy.
- Giữ các biến môi trường Cloudinary, SMTP, JWT, VietQR đã cấu hình. Không gửi mật khẩu hoặc API secret.
- Nếu STS chạy phiên bản cũ, Maven > Update Project, Project > Clean, restart app.

## Những phần bổ sung
- Sắp xếp sản phẩm theo bán chạy (số lượng từ đơn DELIVERED), đánh giá trung bình, lượt yêu thích; trang chủ lấy tối đa 20 sản phẩm theo id giảm dần.
- USER: lưu nhiều địa chỉ, đặt mặc định/xóa tại `/profile/addresses`; sản phẩm đã xem `/recently-viewed` (20 lượt gần nhất).
- USER: yêu cầu trả hàng cho đơn đã giao trong 7 ngày, từ 10–500 ký tự; theo dõi `/my-returns`.
- VENDOR: nhận/từ chối yêu cầu trả hàng; thêm món mới; quản lý chương trình khuyến mãi của shop (lưu/cài trạng thái, **chưa áp dụng tự động vào thanh toán**).
- USER đăng ký shop tại `/vendor/apply`; ADMIN duyệt tại `/admin/applications` (đổi vai trò thành VENDOR, đăng nhập lại).
- ADMIN: tắt/mở shop, cấu hình tỷ lệ chiết khấu và danh mục đối tác vận chuyển tại `/admin/settings` (**chưa tự tính chiết khấu hay tích hợp tính cước vào giao dịch**).
- Đánh giá sản phẩm cho phép đính kèm JPG/PNG/WebP hoặc MP4 qua Cloudinary, tối đa 15MB. Ảnh sản phẩm vẫn giới hạn 5MB trong service.
- SHIPPER: thống kê đơn được giao, hoàn tất và đang thực hiện.

## Thứ tự kiểm thử
1. Guest: trang chủ, lọc/tìm kiếm, 6 kiểu sắp xếp (bao gồm mới, bán chạy, rating, yêu thích), phân trang.
2. USER: đăng nhập, trang sản phẩm (lịch sử xem), lưu 2 địa chỉ, đặt mặc định/xóa, giỏ hàng, size và topping, COD/VietQR.
3. USER: đơn DELIVERED trong 7 ngày → gửi yêu cầu trả hàng; không được gửi trùng hoặc với đơn NEW.
4. VENDOR: xem yêu cầu trả hàng thuộc shop mình, chấp nhận/từ chối và kiểm tra shop khác phải 403; thêm sản phẩm.
5. USER có vai trò USER: gửi đăng ký shop → ADMIN duyệt → đăng nhập lại để trở thành VENDOR.
6. ADMIN: bật tắt shop, thiết lập chiết khấu và đối tác vận chuyển; kiểm tra USER không vào `/admin/**`.
7. USER đã mua hàng giao thành công: đánh giá 50–500 ký tự, đính kèm ảnh hoặc video; thử tệp sai định dạng.
8. SHIPPER: đơn được phân công, chuyển trạng thái, số liệu thống kê.
9. Regression: OTP, forgot password, Cloudinary ảnh sản phẩm, VietQR, voucher, hủy đơn và các trạng thái giao hàng.

## Các mục chưa thể công bố 'đạt yêu cầu' / chưa hoàn thiện
- Hoàn tiền **thực tế** cần thực hiện qua phương thức thanh toán hoặc thủ công có chứng từ; trạng thái `APPROVED` chỉ là xét duyệt yêu cầu, không có bằng chứng tiền đã hoàn. Chưa có quy trình hoàn tiền tự động.
- Manager hiện vẫn là bảng điều khiển toàn cục, chưa quản lý quyền và CRUD theo cửa hàng.
- Khuyến mãi riêng shop, chiết khấu app và giá đối tác vận chuyển đã có cấu hình/danh mục nhưng chưa nối hoàn chỉnh vào checkout/đối soát, chưa có tích hợp đối tác ngoài.
- Chưa kiểm thử giao diện responsive trên thiết bị thực tế, chưa nghiệm thu theo toàn bộ test case, và chưa đối chiếu lịch sử GitHub/phân công nhóm.

Khi test gặp lỗi, gửi ảnh Console STS và URL đang thao tác (ẩn mọi thông tin nhạy cảm).
