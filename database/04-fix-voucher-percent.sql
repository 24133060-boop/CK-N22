-- UTEMILKTEA v2.0: run once in SSMS on UTEMILKTEA_DB BEFORE starting updated app.
-- Preserve existing voucher data. SQL Server treats PERCENT as a reserved keyword.
USE [UTEMILKTEA_DB];
GO
IF OBJECT_ID(N'dbo.vouchers', N'U') IS NOT NULL
BEGIN
    IF COL_LENGTH(N'dbo.vouchers', N'percent') IS NOT NULL
       AND COL_LENGTH(N'dbo.vouchers', N'discount_percent') IS NULL
    BEGIN
        EXEC sp_rename N'dbo.vouchers.percent', N'discount_percent', N'COLUMN';
    END;
    -- If both columns exist (for example, after an earlier automatic update),
    -- copy the old values into the new column without deleting old data.
    IF COL_LENGTH(N'dbo.vouchers', N'percent') IS NOT NULL
       AND COL_LENGTH(N'dbo.vouchers', N'discount_percent') IS NOT NULL
    BEGIN
        EXEC(N'UPDATE dbo.vouchers SET discount_percent = [percent] WHERE [percent] IS NOT NULL');
    END;
END;
GO
