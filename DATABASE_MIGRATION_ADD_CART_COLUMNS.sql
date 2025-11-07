--------------------------------------------------------------------
-- MIGRATION: THÊM COLOR VÀ SIZE VÀO CARTS TABLE
-- Ngày: 2025-11-07
-- Mục đích: Đồng bộ Cart entity với database
--------------------------------------------------------------------
USE ShopOMG;
GO

PRINT '========================================';
PRINT 'KIỂM TRA VÀ THÊM CỘT COLOR, SIZE';
PRINT '========================================';

-- Kiểm tra xem cột color đã tồn tại chưa
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Carts' AND COLUMN_NAME = 'color'
)
BEGIN
    ALTER TABLE Carts ADD color NVARCHAR(50) NULL;
    PRINT '✅ Đã thêm cột color vào bảng Carts';
END
ELSE
BEGIN
    PRINT '⚠️ Cột color đã tồn tại';
END

-- Kiểm tra xem cột size đã tồn tại chưa
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Carts' AND COLUMN_NAME = 'size'
)
BEGIN
    ALTER TABLE Carts ADD size NVARCHAR(50) NULL;
    PRINT '✅ Đã thêm cột size vào bảng Carts';
END
ELSE
BEGIN
    PRINT '⚠️ Cột size đã tồn tại';
END

PRINT '';
PRINT '========================================';
PRINT 'KIỂM TRA CẤU TRÚC BẢNG CARTS';
PRINT '========================================';

-- Hiển thị cấu trúc bảng Carts
SELECT 
    COLUMN_NAME AS [Tên cột],
    DATA_TYPE AS [Kiểu dữ liệu],
    CHARACTER_MAXIMUM_LENGTH AS [Độ dài],
    IS_NULLABLE AS [Nullable]
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Carts'
ORDER BY ORDINAL_POSITION;

PRINT '';
PRINT '✅ MIGRATION HOÀN TẤT!';
GO
