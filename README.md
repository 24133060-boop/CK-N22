# UTEMILKTEA — phiên bản sửa lỗi 0.2

**Trạng thái: source khởi tạo có nghiệp vụ cơ bản; KHÔNG phải đồ án hoàn chỉnh.**

## Yêu cầu
- Java 17, Maven, Eclipse/STS, SQL Server đang chạy tại localhost:14330, tài khoản `sa` (hoặc tài khoản SQL khác có quyền DB).
- Chạy `database/01-create-database.sql` trong SQL Server Management Studio trước.
- Đặt biến môi trường `DB_PASSWORD` bằng mật khẩu SQL Server thực tế. Không commit mật khẩu lên GitHub.
- Import: Eclipse/STS > File > Import > Maven > Existing Maven Projects > chọn thư mục dự án.
- Maven > Update Project; chạy `UteMilkteaApplication` bằng Run As > Spring Boot App hoặc Java Application.
- Mở http://localhost:8080 . Bootstrap dùng CDN nên cần Internet để hiển thị đúng.

## Tài khoản mẫu (chỉ để demo, thay đổi trước khi triển khai công khai)
- `user@utemilktea.local`
- `vendor@utemilktea.local`
- `manager@utemilktea.local`
- `admin@utemilktea.local`
- `shipper@utemilktea.local`
- Mật khẩu chung: `Demo@12345`

## Đã triển khai ở phiên bản 0.1
- Spring Boot MVC + JSP/JSTL + Bootstrap; SQL Server JPA; Spring Security form login, BCrypt và CSRF.
- Tạo dữ liệu mẫu 5 cửa hàng, 30 sản phẩm, 5 tài khoản phân quyền.
- Trang chủ, danh sách sản phẩm theo cửa hàng, tìm kiếm tên sản phẩm.
- Đăng ký tài khoản bằng email/mật khẩu (CHƯA OTP), đăng nhập, đăng xuất.
- Đặt một món/đơn COD với size, đường, đá, topping, số lượng, nhận tại cửa hàng hoặc giao hàng; xem lịch sử đơn.
- Vendor xem đơn và đổi trạng thái (chưa giới hạn cửa hàng theo chủ sở hữu).
- Trang dashboard nền tảng cho Manager/Admin/Shipper.

## CHƯA triển khai — tuyệt đối không xem là chức năng đã hoàn thành
- JWT, OTP email, quên mật khẩu, Sitemesh decorator, Cloudinary upload, WebSocket.
- Giỏ hàng nhiều cửa hàng, checkout tách đơn, phí ship, cộng giá size/topping, tồn kho, địa chỉ giao hàng quản lý theo user.
- VNPay/MoMo sandbox, voucher, tích điểm, đánh giá, yêu thích, phân trang, báo cáo doanh thu.
- Đăng ký shop, phê duyệt shop, phân quyền sở hữu Vendor, Manager theo cửa hàng, quản lý Admin đầy đủ, phân công Shipper.
- Kiểm thử tích hợp và xác nhận build trên máy có Maven/SQL Server.

## Lưu ý bảo mật
- Đây là mã mẫu để học tập, KHÔNG đưa lên Internet công khai trước khi hoàn thiện phân quyền tài nguyên.
- Vendor hiện xem được đơn của tất cả shop và Admin chưa có kiểm soát dữ liệu chi tiết; cần sửa trước khi demo với dữ liệu thật.
- Không đưa secret SQL Server, email, Cloudinary, cổng thanh toán lên GitHub.

## Tiếp tục phát triển
Xem `docs/KE_HOACH_HOAN_THIEN.md`. Cần chạy và kiểm thử từng chức năng sau khi bổ sung.

## Sửa lỗi phiên bản 0.2
- Cho phép Spring Security xử lý FORWARD/ERROR dispatcher tới JSP, tránh vòng lặp `/login` (`ERR_TOO_MANY_REDIRECTS`).
- Áp dụng phân quyền cả `/admin`, `/manager`, `/vendor`, `/shipper` (không chỉ đường dẫn có dấu `/` phía sau).
- Đặt cổng SQL Server mặc định thành **14330** theo kết quả kiểm tra trên máy người dùng; có thể thay bằng biến môi trường `DB_PORT`.
- Nếu vẫn có lỗi, xem Console và xác minh database đã tạo, mật khẩu qua `DB_PASSWORD`.
- Chưa có điều kiện kiểm thử trực tiếp với SQL Server/STS của người dùng; đây là bản sửa mã nguồn, không phải cam kết đã chạy thành công.


## v0.3 - JSP EL hotfix
Bổ sung JavaBeans getters cho các entity Shop, Product, AppUser và CustomerOrder để JSP truy cập `${s.name}`, `${p.name}` và các thuộc tính khác. Giữ nguyên cấu hình bảo mật từ v0.2. Chưa xác nhận chạy thực tế trên SQL Server của người dùng.


## Cập nhật v0.6
Xem `docs/PHAN_QUYEN_VA_LO_TRINH_V06.md` để biết phần đã triển khai và chưa triển khai.
