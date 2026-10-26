# Đối chiếu yêu cầu và hiện trạng UTEMILKTEA v2.1

Nguồn: `docs/KE_HOACH_HOAN_THIEN.md` trong dự án, và mã nguồn v2.0 người dùng đã kiểm thử. Đây là kiểm tra tĩnh; **không** thay thế chạy thực tế trên SQL Server.

| Yêu cầu | Trạng thái trong mã nguồn | Ghi chú |
|---|---|---|
| Spring Boot, JSP/JSTL, Bootstrap, JPA, SQL Server | Có | Xem `pom.xml`, `application.properties`, `WEB-INF/views` |
| USER/VENDOR/MANAGER/ADMIN/SHIPPER | Có | Spring Security URL + kiểm tra quyền sở hữu tại một số controller |
| Đặt hàng đa chi nhánh | Có | Tách đơn theo shop trong `CartService`; transaction bao quanh checkout |
| Giao hàng/nhận tại quầy | Có | Bản đồ, tính phí vận chuyển demo; cần kết nối Internet |
| Voucher | Có | Kiểm tra mức tối thiểu, phần trăm, giới hạn giảm; cột `discount_percent` SQL Server |
| Hủy đơn, phân công shipper, trạng thái | Có | Kiểm tra chuyển trạng thái trong controller |
| Yêu thích/đánh giá/doanh thu | Có | Dựa trên mã nguồn và người dùng đã test |
| OTP email đăng ký/khôi phục mật khẩu | **Chưa có** | Đăng ký trực tiếp, chưa có xác thực email |
| Giỏ hàng lưu DB | **Chưa có** | Giỏ hàng đang lưu `HttpSession` |
| Cloudinary | **Chưa có** | Không có dependency/tích hợp |
| JWT | **Chưa có** | Hiện dùng session form login Spring Security |
| WebSocket realtime | **Chưa có** | Chưa có dependency/cấu hình WebSocket |
| Sitemesh | **Chưa có** | Chưa có dependency/cấu hình |
| VNPay/MoMo sandbox | **Chưa có** | Chưa có cổng thanh toán, cần credentials |
| Kiểm thử tự động | **Một phần** | Thêm test voucher; chưa có test tích hợp SQL Server |
| Video có mặt 3 thành viên, GitHub commit thật | **Không xác minh được từ ZIP** | Cần nhóm chuẩn bị ngoài mã nguồn |

## Thay đổi trong v2.1
- Bổ sung kiểm tra tọa độ giao hàng ở tầng nghiệp vụ trước khi lưu đơn.
- Dùng ngày Việt Nam khi xét voucher hết hạn.
- Chặn cấu hình voucher có mức tối thiểu âm tại tầng dịch vụ.
- Không hiển thị nội dung exception trên trang lỗi HTTP mặc định.
- Thêm kiểm thử JUnit cho công thức giảm giá.
- Giữ nguyên nghiệp vụ đã kiểm thử; chưa tự ý thêm các tích hợp yêu cầu tài khoản/secret.

## Rủi ro và việc cần làm trước khi nộp
1. Không nên tuyên bố đáp ứng đủ rubric: các mục **chưa có** phía trên là khoảng trống thực sự.
2. `application.properties` có mật khẩu SQL Server mặc định cho môi trường demo: nên thiết lập `DB_PASSWORD` qua biến môi trường và không đưa mật khẩu thật lên GitHub.
3. Cần kiểm thử cạnh tranh cập nhật trạng thái, xử lý lỗi commit và mã giảm giá khi thanh toán nhiều chi nhánh; mã voucher hiện áp dụng **mỗi đơn chi nhánh**.
4. Chạy `mvn test` và thử toàn bộ luồng trong STS trên SQL Server trước khi chốt bản nộp.
