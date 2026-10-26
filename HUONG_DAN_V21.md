# UTEMILKTEA v2.1 — rà soát và tăng an toàn

1. **Sao lưu** `UTEMILKTEA_DB` và giữ bản ZIP v2.0 đã chạy tốt.
2. Nếu DB đã chạy v2.0 fix voucher thì **không cần chạy lại script** `database/04-fix-voucher-percent.sql`.
3. Import project bằng STS → Maven → Update Project → Project → Clean.
4. Cấu hình `DB_PORT`, `DB_USER`, `DB_PASSWORD` theo SQL Server trên máy.
5. Chạy `mvn test` (nếu có Maven) rồi chạy ứng dụng, kiểm thử trang `/manager/vouchers`, checkout, my-orders và phân quyền.
6. Đọc `docs/DOI_CHIEU_YEU_CAU_V21.md` để xem **các yêu cầu còn thiếu**.

Bản này **chưa được kiểm thử tích hợp với SQL Server của người dùng**; không phải bản đáp ứng toàn bộ rubric.
