# Bộ lọc sản phẩm

Mở `/products/search` từ trang chủ. Có thể lọc kết hợp theo tên, danh mục, chi nhánh, giá từ/đến; sắp xếp theo tên, giá, sản phẩm mới (ID giảm dần). Phân trang 20 món. Chỉ hiển thị sản phẩm đang bán thuộc shop đang hoạt động.

## Test
1. Lọc một danh mục và một chi nhánh, đối chiếu kết quả.
2. Lọc giá từ 20000 đến 40000; mọi sản phẩm phải trong khoảng.
3. Chọn giá tăng/giảm và kiểm tra thứ tự.
4. Kết hợp tên + danh mục + giá; chuyển trang không mất điều kiện.
5. Nhập giá tối thiểu lớn hơn tối đa: nhận thông báo 400.

Chưa thêm bộ lọc đánh giá, bán chạy vì dữ liệu hiện tại chưa đủ đáng tin để xếp hạng; không nên tạo nhãn xếp hạng giả. Chưa chạy tích hợp với SQL Server tại môi trường này.
