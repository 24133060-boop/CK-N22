# Đặc tả và kế hoạch hoàn thiện UTEMILKTEA

## Bối cảnh
Đề tài 12 — xây dựng website chuỗi bán trà sữa bằng Spring Boot + JSP/JSTL + Sitemesh + Bootstrap + JPA + SQL Server + JWT + WebSocket + Cloudinary. Một thương hiệu, nhiều cửa hàng, Vendor quản lý từng cửa hàng, Manager quản lý theo phạm vi, Admin toàn hệ thống, Shipper giao đơn.

## Giao diện
Tối giản, hiện đại, xanh lá + trắng + kem; responsive. Khách chọn cửa hàng, món, size S/M/L, đường, đá, topping, số lượng, ghi chú. Hỗ trợ giao hàng hoặc nhận tại quầy.

## Các mốc ưu tiên
1. An toàn: quan hệ chủ cửa hàng, phân quyền theo từng bản ghi, kiểm tra trạng thái đơn, validation, transaction, kiểm thử.
2. Guest/User: OTP email đăng ký/khôi phục mật khẩu; giỏ hàng lưu DB; nhiều shop tách đơn; lịch sử, địa chỉ, hủy đơn, yêu thích, đánh giá, phân trang.
3. Vendor: đăng ký shop, phê duyệt, CRUD sản phẩm, hình Cloudinary, đơn hàng, voucher, doanh thu.
4. Manager/Admin: quản lý shop/user/danh mục/chiết khấu/nhà vận chuyển, phân quyền, báo cáo.
5. Shipper: phân công, nhận đơn, cập nhật trạng thái, thống kê.
6. JWT đúng mô hình bảo mật, WebSocket realtime, VNPay/MoMo sandbox (cần tài khoản và secret riêng), Sitemesh, responsive polish.
7. Kiểm thử, video có mặt 3 thành viên, tài liệu báo cáo, link GitHub với commit thật, SQL, datasheet AI nếu có.

## Kiểm thử bắt buộc
- Không đăng nhập không thể đặt đơn; user A không thể xem đơn user B.
- Vendor A không thể xem hoặc sửa đơn, sản phẩm của Vendor B.
- Chỉ trạng thái hợp lệ được chuyển tiếp; đơn đã hủy không thể giao.
- Tính tổng tiền đúng với size, topping, voucher, phí ship.
- OTP hết hạn, giới hạn gửi lại, hash mật khẩu; JWT hết hạn/thu hồi phù hợp.
- Checkout nhiều cửa hàng không làm sai tồn kho khi lỗi; thanh toán callback xác thực chữ ký, idempotent.
- Kiểm thử mobile và giao diện từng role.

## Ghi chú
Bản 0.1 là bản nền tảng để xem cấu trúc và góp ý, chưa đáp ứng đủ rubric. Không nên nộp bản này làm bài cuối kỳ.
