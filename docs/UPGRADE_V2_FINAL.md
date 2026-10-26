# UTEMILKTEA — Gói nâng cấp tích hợp

Được xây trên bản v2.0 đợt 2 đã sửa đánh giá.

Bổ sung: voucher phần trăm với giá trị đơn tối thiểu, mức giảm tối đa, ngày hết hạn và bật/tắt; áp dụng theo từng đơn/chi nhánh khi checkout; chi tiết đơn hàng; doanh thu tiền món cho Vendor.

Lưu ý: chưa tích hợp thanh toán VNPay/MoMo, OTP email, cấu hình phí vận chuyển động, biểu đồ báo cáo nâng cao, và chưa xác nhận chạy thực tế SQL Server. Không xem đây là bản hoàn thành 100% mọi yêu cầu ban đầu.

Test: Manager /manager/vouchers tạo WELCOME10 (10%, min 0, max 20000); USER đặt hàng và nhập WELCOME10; mở chi tiết đơn từ /my-orders; Vendor xem doanh thu.

Sao lưu DB trước khi chạy. Hibernate ddl-auto=update sẽ thêm bảng vouchers và cột vào customer_orders.
