# UTEMILKTEA v1.1 demo – Bản đồ và phí giao hàng

## Thay đổi
- 5 chi nhánh dùng **tọa độ gần khu vực được nêu**, không phải địa chỉ cửa hàng được xác minh. Chỉ để demo.
- Khách mở **Giỏ hàng**, chọn **Giao tận nơi**, bấm điểm trên bản đồ OpenStreetMap và nhập địa chỉ chi tiết.
- Trang giỏ hiển thị đường đi từ mỗi chi nhánh, km đường đi, phí giao hàng và ETA ước tính.
- Khi đặt COD, backend tự gọi OSRM để tính lại, không tin giá trị tính ở trình duyệt. Mỗi chi nhánh tạo 1 đơn; tổng tiền đơn = tổng tiền món + phí giao hàng riêng của chi nhánh đó.
- Nhận tại cửa hàng: không thu phí ship. Mua trực tiếp một món tại trang sản phẩm chỉ hỗ trợ PICKUP; muốn giao hàng hãy thêm vào giỏ.
- Phí demo cố định trong `DeliveryMapService.java`: 12.000đ + 4.000đ * ceil(km); giới hạn 25 km đường đi; ETA demo = 10 phút chuẩn bị + thời gian lái xe OSRM.

## Vận hành
1. Sao lưu SQL Server database UTEMILKTEA_DB trước khi cập nhật.
2. Giải nén ZIP và Import Existing Maven Projects trong STS; Maven > Update Project, Project > Clean.
3. Chạy Spring Boot App với Java 17, SQL Server theo cấu hình hiện tại.
4. Đăng nhập USER, thêm món từ 2 chi nhánh, vào `/cart`, chọn điểm giao trên bản đồ và nhập số nhà/đường, đợi phí hiển thị, đặt COD.
5. Kiểm tra `/my-orders`: 2 đơn, mỗi đơn có phí ship và tổng tiền riêng. Thử PICKUP: phí ship = 0.

## Lưu ý
- Dùng Leaflet/OpenStreetMap và máy chủ định tuyến công cộng OSRM; cần Internet. OSRM công cộng **không cam kết ổn định**, chỉ dùng demo/học tập; nếu lỗi thì không cho đặt giao hàng để tránh thu sai phí.
- Không có GPS theo dõi Shipper; ETA là ước tính, không phải cam kết.
- `ddl-auto=update` có thể thêm các cột tọa độ/phí ship vào SQL Server; sao lưu dữ liệu trước.
- Chưa có giao diện cấu hình phí ship cho Manager/Admin. Mức phí hiện đặt trong mã Java; cần triển khai riêng nếu yêu cầu cấu hình động.
- Không chạy thử Maven tại môi trường tạo ZIP vì không có lệnh `mvn`; cần kiểm tra compile/run trên STS của bạn.
