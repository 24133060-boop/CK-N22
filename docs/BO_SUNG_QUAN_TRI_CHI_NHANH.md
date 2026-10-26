# Quản trị theo chi nhánh – hướng dẫn kiểm thử

1. ADMIN đăng nhập, vào `/manager/catalog` và phân công tài khoản có role MANAGER cho từng chi nhánh.
2. MANAGER đăng nhập lại, truy cập `/manager/catalog`, chỉ thấy chi nhánh được phân công.
3. Kiểm tra sửa tên, danh mục, giá, bật/tắt sản phẩm; thêm sản phẩm; đổi tên danh mục; bật/tắt khuyến mãi.
4. Thử sửa trực tiếp URL của sản phẩm thuộc chi nhánh khác, hệ thống phải trả 403.
5. ADMIN có thể chỉnh tên/địa chỉ shop và đổi/gỡ phân công.
6. Dữ liệu từ các chi nhánh khác phải không bị ảnh hưởng.

Giới hạn: chương trình khuyến mãi ở màn hình này hiện là cấu hình quản lý; chưa tự động giảm tiền đơn hàng. Không tự tuyên bố đã đối soát thanh toán hoặc hoàn tiền thực tế.
