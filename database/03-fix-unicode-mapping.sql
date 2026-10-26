-- UTEMILKTEA v0.5: Dong bo Unicode voi SQL Server.
-- Sao luu database truoc khi chay. Chay script v0.4 (02-fix-vietnamese.sql) truoc neu du lieu mau van bi loi dau.
USE UTEMILKTEA_DB;
GO
-- Chuyen cac cot van ban sang NVARCHAR. Khong xoa bang, khong xoa du lieu.
IF OBJECT_ID(N'dbo.shop', N'U') IS NOT NULL
BEGIN
 ALTER TABLE dbo.shop ALTER COLUMN name NVARCHAR(255) NULL;
 ALTER TABLE dbo.shop ALTER COLUMN address NVARCHAR(255) NULL;
END;
IF OBJECT_ID(N'dbo.product', N'U') IS NOT NULL
BEGIN
 ALTER TABLE dbo.product ALTER COLUMN name NVARCHAR(255) NULL;
 ALTER TABLE dbo.product ALTER COLUMN category NVARCHAR(255) NULL;
END;
IF OBJECT_ID(N'dbo.app_users', N'U') IS NOT NULL
 ALTER TABLE dbo.app_users ALTER COLUMN display_name NVARCHAR(255) NOT NULL;
IF OBJECT_ID(N'dbo.customer_orders', N'U') IS NOT NULL
BEGIN
 ALTER TABLE dbo.customer_orders ALTER COLUMN product_name NVARCHAR(255) NULL;
 ALTER TABLE dbo.customer_orders ALTER COLUMN sugar NVARCHAR(255) NULL;
 ALTER TABLE dbo.customer_orders ALTER COLUMN ice NVARCHAR(255) NULL;
 ALTER TABLE dbo.customer_orders ALTER COLUMN topping NVARCHAR(255) NULL;
 ALTER TABLE dbo.customer_orders ALTER COLUMN address NVARCHAR(255) NULL;
END;
GO
SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS
 WHERE TABLE_NAME IN ('shop','product','app_users','customer_orders') AND DATA_TYPE IN ('varchar','nvarchar','nchar')
 ORDER BY TABLE_NAME,COLUMN_NAME;
GO
