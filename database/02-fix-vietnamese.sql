-- UTEMILKTEA v0.4 - Sửa dữ liệu tiếng Việt đã bị mất dấu
-- Chạy trong SSMS bằng tài khoản có quyền ALTER TABLE / UPDATE.
-- Sao lưu database trước khi chạy nếu bạn đã nhập dữ liệu thật.
USE UTEMILKTEA_DB;
GO
-- SQL Server cần NVARCHAR để lưu Unicode ổn định.
IF OBJECT_ID(N'dbo.shop',N'U') IS NOT NULL
BEGIN
    ALTER TABLE dbo.shop ALTER COLUMN name NVARCHAR(255) NULL;
    ALTER TABLE dbo.shop ALTER COLUMN address NVARCHAR(255) NULL;
END;
IF OBJECT_ID(N'dbo.product',N'U') IS NOT NULL
BEGIN
    ALTER TABLE dbo.product ALTER COLUMN name NVARCHAR(255) NULL;
    ALTER TABLE dbo.product ALTER COLUMN category NVARCHAR(255) NULL;
END;
GO
-- Sửa các cửa hàng dữ liệu mẫu đã được tạo ở phiên bản trước.
UPDATE dbo.shop SET name=N'UTEMILKTEA Thủ Đức' WHERE name LIKE 'UTEMILKTEA Th%';
UPDATE dbo.shop SET name=N'UTEMILKTEA Quận 1' WHERE name LIKE 'UTEMILKTEA Qu%';
UPDATE dbo.shop SET name=N'UTEMILKTEA Bình Thạnh' WHERE name LIKE 'UTEMILKTEA B%';
UPDATE dbo.shop SET name=N'UTEMILKTEA Gò Vấp' WHERE name LIKE 'UTEMILKTEA G%';
UPDATE dbo.shop SET name=N'UTEMILKTEA Dĩ An' WHERE name LIKE 'UTEMILKTEA D%';
UPDATE dbo.shop SET address=N'Địa chỉ minh họa — cập nhật trước khi demo'
 WHERE address LIKE '%minh h%';
-- Các sản phẩm mẫu được tạo với giá 29.000, 33.000, ... 49.000 đồng.
UPDATE dbo.product SET name=CASE CAST(price AS INT)
 WHEN 29000 THEN N'Trà sữa truyền thống'
 WHEN 33000 THEN N'Trà sữa matcha'
 WHEN 37000 THEN N'Trà đào cam sả'
 WHEN 41000 THEN N'Trà sữa ô long'
 WHEN 45000 THEN N'Trà sữa khoai môn'
 WHEN 49000 THEN N'Trà vải hoa hồng'
 ELSE name END
WHERE CAST(price AS INT) IN (29000,33000,37000,41000,45000,49000)
 AND (name LIKE 'Tr%' OR name LIKE '%?%');
UPDATE dbo.product SET category=N'Trà sữa' WHERE category LIKE 'Tr%';
SELECT id,name,address FROM dbo.shop ORDER BY id;
SELECT TOP (10) id,name,category,price FROM dbo.product ORDER BY id;
GO
