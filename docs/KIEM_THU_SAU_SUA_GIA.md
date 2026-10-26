# Kiểm thử sau khi sửa lỗi ô nhập giá

Đã sửa ô giá tại MANAGER `/manager/catalog` và VENDOR `/vendor`: `step=1`, hiển thị số VND dạng nguyên không dấu phân cách bằng JSTL `fmt:formatNumber`. Giữ nguyên các tính năng đã test.

1. MANAGER: mở chi nhánh có giá 29.000đ. Ô giá phải hiện `29000`, không có `,00`. Nhấn Lưu không sửa giá: trình duyệt không báo step mismatch.
2. MANAGER: sửa thành `30000`, Lưu, tải lại; giữ `30000`. Thử giá `0` bị chặn; `29000.5` bị chặn.
3. VENDOR: mở danh sách sản phẩm, thử lưu nguyên giá và đổi giá nguyên.
4. VENDOR: thêm sản phẩm giá `29000`, kiểm tra thành công.
5. Kiểm tra lại USER: sản phẩm hiển thị giá, giỏ hàng và tính tiền đúng.

Các thay đổi này chưa được chạy với SQL Server/STS trong môi trường tạo ZIP.
