# UTEMILKTEA – Website quản lý chuỗi cửa hàng trà sữa

## 1. Giới thiệu

UTEMILKTEA là website bán trà sữa và quản lý nhiều chi nhánh, được xây dựng phục vụ đồ án môn học. Hệ thống cho phép khách hàng tìm kiếm, đặt món, thanh toán và theo dõi đơn hàng. Bên cạnh đó, cửa hàng và bộ phận quản lý có các chức năng xử lý đơn, quản lý sản phẩm, chương trình khuyến mãi và hoạt động kinh doanh.

## 2. Công nghệ sử dụng

- Java 17, Spring Boot, Spring MVC, Spring Security và Spring Data JPA.
- JSP/JSTL, Bootstrap, HTML, CSS và JavaScript.
- Microsoft SQL Server để lưu dữ liệu.
- Email OTP cho đăng ký và khôi phục mật khẩu; BCrypt để mã hóa mật khẩu.
- Cloudinary quản lý hình ảnh; bản đồ hỗ trợ chọn địa chỉ giao hàng và ước tính phí vận chuyển.
- VietQR để hiển thị thông tin chuyển khoản; COD để thanh toán khi nhận hàng.

## 3. Chức năng của hệ thống

### Guest (khách chưa đăng nhập)

- Xem trang chủ, chi nhánh và danh sách món uống.
- Tìm kiếm, lọc và sắp xếp sản phẩm theo các tiêu chí.
- Đăng ký tài khoản và xác thực bằng OTP gửi qua email.

### User (khách hàng)

- Đăng nhập, đăng xuất, khôi phục mật khẩu và xem hồ sơ cá nhân.
- Quản lý các địa chỉ nhận hàng; xem sản phẩm đã xem và danh sách yêu thích.
- Chọn chi nhánh, tùy chỉnh size, lượng đường, đá, topping và số lượng món.
- Thêm, cập nhật, xóa món trong giỏ hàng; đặt hàng từ một hoặc nhiều chi nhánh.
- Chọn nhận tại cửa hàng hoặc giao tận nơi; dùng bản đồ chọn địa điểm nhận và xem phí vận chuyển dự kiến.
- Áp dụng voucher, thanh toán COD hoặc nhận mã VietQR để chuyển khoản.
- Xem lịch sử, chi tiết và trạng thái đơn; hủy đơn chưa được xác nhận theo quy định.
- Đánh giá món đã mua, gửi yêu cầu trả hàng và theo dõi kết quả hoàn tiền.

### Vendor (cửa hàng)

- Có các chức năng mua hàng của User và các chức năng quản lý cửa hàng.
- Gửi yêu cầu mở cửa hàng; quản lý sản phẩm, ảnh, giá và tình trạng còn hàng.
- Quản lý thời gian hoạt động, khuyến mãi và đơn hàng của chi nhánh mình phụ trách.
- Xác nhận, chuẩn bị đơn, phối hợp phân công giao hàng và theo dõi kết quả.
- Xử lý yêu cầu trả hàng; ghi nhận đã hoàn tiền sau khi tiền được hoàn bên ngoài hệ thống.
- Theo dõi các đơn và thông tin kinh doanh của cửa hàng.

### Manager (quản lý chi nhánh)

- Quản lý sản phẩm, danh mục, khách hàng và chương trình khuyến mãi trong phạm vi chi nhánh được phân công.
- Theo dõi doanh thu, đơn hàng và hoạt động tại chi nhánh.
- Thiết lập chiết khấu, lựa chọn đơn vị vận chuyển cho chi nhánh theo quyền được cấp.

### Admin (quản trị hệ thống)

- Quản lý tài khoản, vai trò, chi nhánh và hoạt động chung của hệ thống.
- Quản lý sản phẩm, danh mục, cấu hình chiết khấu và đơn vị vận chuyển.
- Duyệt yêu cầu mở cửa hàng và theo dõi báo cáo kinh doanh.

### Shipper (nhân viên giao hàng)

- Xem danh sách đơn hàng được phân công.
- Xác nhận nhận hàng, cập nhật giao hàng thành công theo đúng trình tự.
- Theo dõi số đơn được giao, đang giao và đã giao.

## 4. Quy trình hoạt động chính

Khách truy cập website → đăng ký/xác thực tài khoản → chọn chi nhánh và món uống → thêm vào giỏ hàng → chọn hình thức nhận hàng → áp dụng giảm giá (nếu có) → đặt đơn và chọn COD/VietQR → cửa hàng tiếp nhận, chuẩn bị → giao hàng hoặc nhận tại quầy → khách theo dõi và đánh giá.

Đối với trả hàng, khách gửi yêu cầu sau khi nhận đơn. Cửa hàng xem xét và phê duyệt hoặc từ chối. Nếu được duyệt, việc hoàn tiền được thực hiện ngoài hệ thống; nhân viên ghi nhận số tiền, thời gian, người xác nhận và mã đối soát. Website **không tự động chuyển tiền từ ngân hàng**.

## 5. Hướng dẫn chạy dự án

1. Chuẩn bị JDK 17, Eclipse/STS, Maven và Microsoft SQL Server.
2. Tạo database theo tập lệnh trong thư mục `database` nếu chưa có dữ liệu.
3. Kiểm tra cấu hình kết nối trong `src/main/resources/application.properties` (máy phát triển dùng SQL Server tại `localhost:14330`).
4. Cung cấp thông tin kết nối cơ sở dữ liệu, email và Cloudinary bằng cấu hình phù hợp; không công khai mật khẩu hoặc API Secret.
5. Trong STS, chọn **Import → Maven → Existing Maven Projects**, chọn thư mục `UTEMILKTEA`, sau đó cập nhật Maven Project.
6. Chạy ứng dụng Spring Boot và mở `http://localhost:8080`.

## 6. Ghi chú

Đây là dự án phục vụ học tập. Thanh toán VietQR và hoàn tiền đang sử dụng xác nhận thủ công; các giao dịch thực tế cần được đối chiếu độc lập. Các chức năng cần được kiểm thử với SQL Server và cấu hình dịch vụ trên máy chạy trước khi trình bày chính thức.
