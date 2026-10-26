# Rà soát UTEMILKTEA theo ảnh đề bài 21/10/2026

Nguồn: ảnh đề do người dùng gửi; mã nguồn kiểm tra: `UTEMILKTEA_FIX_VENDOR_GIU_VI_TRI_DON_HANG.zip`. Đây là rà soát tĩnh theo mã nguồn, KHÔNG phải xác nhận kiểm thử tích hợp, hiệu năng hay bảo mật hoàn toàn.

## Trạng thái so với từng yêu cầu

| Yêu cầu gốc | Bằng chứng/giới hạn | Kết luận |
|---|---|---|
| Chung: tìm kiếm/lọc | `WebController.search`, `search.jsp`: tên, danh mục, chi nhánh, khoảng giá, các cách sắp xếp | Có mã nguồn; USER đã test |
| Chung: đăng ký/kích hoạt qua email OTP, đăng nhập/đăng xuất, quên mật khẩu OTP | `RegistrationController`, `PasswordResetController`, Spring Security | Có mã nguồn; USER đã test |
| Chung: mã hóa mật khẩu, Spring Security | Security config + PasswordEncoder | Có triển khai; cần rà kiểm quyền trên từng endpoint |
| Guest: Trang chủ hơn 10 sản phẩm xếp giảm dần | `home.jsp` có 12 thẻ; `WebController.home` chọn tối đa 12 sản phẩm còn bán, ưu tiên có ảnh rồi sắp xếp ID giảm dần | Đạt tiêu chí hiển thị nếu DB có ít nhất 11 món đang bán; tiêu chí “giảm dần” theo ID, cần xác nhận giảng viên muốn sắp theo đại lượng nào |
| User: sản phẩm theo danh mục; 20 sản phẩm mới/bán chạy/đánh giá/yêu thích; phân trang | `WebController.search` có sắp xếp theo nhiều tiêu chí và phân trang 20 | Có mã nguồn; cần xác nhận ý “20 sản phẩm ... nhất” là 4 danh sách riêng hay một danh sách chọn kiểu |
| User: hồ sơ nhiều địa chỉ | `DeliveryAddress`, `ExtendedFeaturesController`, `addresses.jsp` | USER đã test |
| User: chi tiết, giỏ, COD và online | `order.jsp`, `CartService`, `OrderService`, VietQR/VNPay controllers | Có mã nguồn; VietQR không đồng nghĩa tự đối soát ngân hàng |
| User: lịch sử đơn và trạng thái | `WebController`, `CustomerOrder`, `my-orders.jsp` | Có mã nguồn; cần test đủ mọi chuyển trạng thái |
| User: trả hàng - hoàn tiền | `ReturnRequest`, `ExtendedFeaturesController` | Có workflow yêu cầu/duyệt; KHÔNG tự hoàn tiền về tài khoản ngân hàng |
| User: yêu thích, đã xem | `Favorite`, `RecentlyViewed` và controllers | Có mã nguồn |
| User: bình luận >=50 ký tự, đánh giá ảnh/video | `WebController.reviewProduct`, `ProductReview`, Cloudinary | USER đã test ảnh; video chưa xác nhận vì proxy chặn ở lượt trước |
| User: voucher | `Voucher`, `WebController`, `OrderService` | Có mã nguồn; USER đã test cơ bản |
| Vendor: đăng ký shop | `ShopApplication`, `ExtendedFeaturesController` + trang duyệt của admin | Có mã nguồn |
| Vendor: quản lý shop/sản phẩm/đơn/trả hàng/doanh thu | `WebController` + `ExtendedFeaturesController`, vendor UI | Có mã nguồn; chưa đủ bằng chứng quy trình tất cả trạng thái |
| Vendor: chương trình khuyến mãi shop | `ShopPromotion`, `ExtendedFeaturesController` | Có CRUD/toggle; chưa thấy chắc chắn nối giảm giá trực tiếp vào OrderService |
| Manager: user, sản phẩm từng shop, danh mục, chiết khấu, khuyến mãi, vận chuyển | `WebController.manager` hiện chủ yếu dashboard; một số chức năng chỉ có ở `/admin/...` | CHƯA ĐỦ theo đúng yêu cầu manager; cần phân quyền thao tác nghiệp vụ theo phạm vi cửa hàng |
| Admin: các phần quản lý tương đương phạm vi toàn hệ thống | `WebController.admin`, `ExtendedFeaturesController` admin settings, applications | Có một phần; cần xác minh CRUD users/categories/all-shop products, chương trình KM/phí vận chuyển |
| Shipper: được phân công và thống kê | `WebController` shipper, đơn giao | USER đã test |
| Bootstrap template, responsive | Các JSP dùng Bootstrap 5.3.3 | Có; chưa test breakpoint toàn bộ thiết bị |
| Mô hình 3 tầng | `controller/`, `service/`, `repository/` | Có cấu trúc; một số xử lý trực tiếp tại controller cần tách để đúng chuẩn hơn |
| Github lịch sử commit/phân công thành viên | Không xác minh được qua ZIP nộp | CHƯA XÁC MINH |
| Báo cáo phân công từng role | Không nằm trong phạm vi ZIP mã nguồn | CHƯA XÁC MINH |

## Sửa lỗi giao diện ảnh đợt này
- Ảnh được gán theo **product.id**, KHÔNG được chia sẻ giữa các món trùng tên ở chi nhánh khác nhau.
- Trang chủ chọn ưu tiên sản phẩm đã có `imageUrl` trong số sản phẩm còn bán/cửa hàng hoạt động, bổ sung món chưa có ảnh cho đủ 12; **danh sách hiển thị cuối cùng sắp ID giảm dần**.
- Giao diện home đã có điều kiện `p.imageUrl` và fallback hình mặc định; không sửa URL ảnh trong DB, không sao chép ảnh giữa các sản phẩm.
- Tiền trên trang home/tìm kiếm hiển thị số nguyên có phân tách hàng nghìn.

## Phần cần làm trước khi khẳng định 100% đề
1. Hoàn thiện CRUD và phân quyền Manager theo shop (users/products/categories, commission, promotions, delivery provider), và Admin cho toàn hệ thống.
2. Kết nối chính sách khuyến mãi shop, chiết khấu, vận chuyển vào cách tính giá/đơn thực tế nếu giảng viên yêu cầu nghiệp vụ hoạt động chứ không chỉ có màn hình cấu hình.
3. Đối soát giao dịch online và hoàn tiền thực tế (hoặc nêu rõ trong báo cáo đây là mô phỏng).
4. Test video đánh giá, các chuyển trạng thái đơn, kiểm tra quyền bằng yêu cầu truy cập trái phép và phân trang/ảnh trên trình duyệt.
5. Chụp minh chứng và kiểm tra GitHub commit theo phân công nhóm; hoàn thiện báo cáo.

## Cách test ảnh trang chủ
- Đăng nhập Vendor, xác định **tên sản phẩm + chi nhánh + id** sản phẩm đã upload ảnh.
- Tải `/products/search` tìm đúng sản phẩm rồi so sánh với 12 sản phẩm featured ở `/`.
- Nếu ảnh đã upload ở một chi nhánh khác, bản ghi product khác sẽ vẫn có fallback tới khi upload riêng cho nó.
