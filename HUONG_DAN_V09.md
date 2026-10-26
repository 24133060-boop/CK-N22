# UTEMILKTEA v0.9 — Bản kiểm thử giỏ hàng

## Đã bổ sung
- Giỏ hàng theo phiên đăng nhập (HttpSession), chỉ vai trò USER được sử dụng.
- Thêm món với size/đường/đá/topping/số lượng, xóa từng dòng giỏ hàng.
- Giỏ chứa món của nhiều chi nhánh, hiện rõ tên chi nhánh.
- Thanh toán COD tạo một đơn cho **mỗi dòng giỏ hàng**, giữ chi nhánh gốc của món.
- Nếu một đơn không thể tạo (ví dụ cửa hàng đã đóng), toàn bộ thao tác tạo đơn được rollback ở tầng giao dịch.
- Dữ liệu giỏ chỉ lưu trong phiên; không phải giỏ hàng database, không đồng bộ nhiều thiết bị.

## CHƯA triển khai — không được coi là hoàn thiện
- Gộp nhiều dòng giỏ của cùng cửa hàng vào một đơn có nhiều order items.
- Bản đồ chọn địa chỉ, định tuyến, phí ship, ETA.
- OTP email, JWT, WebSocket, Cloudinary, thanh toán VNPay/MoMo.
- Dashboard CRUD đầy đủ cho Manager/Admin, voucher/tích điểm.
- Kiểm thử chạy thật trên SQL Server của người dùng.

## Kiểm thử đề xuất
1. Đăng nhập USER, vào menu chọn món, bấm **Thêm vào giỏ hàng**.
2. Thêm món từ một chi nhánh khác; kiểm tra giỏ có hai chi nhánh.
3. Xóa một dòng và thêm lại.
4. Chọn DELIVERY, không nhập địa chỉ: phải hiện lỗi và giữ giỏ.
5. Nhập địa chỉ, xác nhận COD: chuyển đến Đơn hàng của tôi, giỏ được xóa.
6. Đăng nhập SHIPPER, truy cập /cart: phải nhận HTTP 403.
7. Kiểm tra Vendor của từng chi nhánh chỉ thấy đơn thuộc chi nhánh đó.
8. Đặt hàng ngoài giờ nhận đơn: báo lỗi, giỏ còn nguyên.

## Chạy
Giải nén ZIP, import Existing Maven Projects trong STS, Maven Update, kiểm tra
application.properties theo môi trường SQL Server của bạn, chạy Spring Boot App.
Sao lưu database trước khi chạy. Không chia sẻ mật khẩu hoặc API key.

## Ghi chú
Phiên bản này là bản phát triển trung gian, chưa phải bản tổng hợp đầy đủ theo
đặc tả. Chưa có môi trường Maven và SQL Server để xác nhận build/runtime.
