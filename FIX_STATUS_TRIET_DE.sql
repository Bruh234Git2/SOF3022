--------------------------------------------------------------------
-- SCRIPT SỬA TRIỆT ĐỂ LỖI TRẠNG THÁI ĐỠN HÀNG
-- Chạy script này TRƯỚC KHI test lại chức năng
--------------------------------------------------------------------
USE ShopOMG;
GO

PRINT '========================================';
PRINT 'BƯỚC 1: KIỂM TRA TRẠNG THÁI HIỆN TẠI';
PRINT '========================================';

-- Xem tất cả trạng thái hiện có
SELECT 
    id,
    status,
    LEN(status) AS length,
    CAST(status AS VARBINARY(50)) AS hex_value,
    CASE 
        WHEN status LIKE '%"%' THEN 'CÓ DẤU NGOẶC KÉP'
        WHEN LEN(status) > 10 THEN 'QUÁ DÀI'
        WHEN status COLLATE Latin1_General_BIN NOT IN ('PENDING', 'SHIPPING', 'COMPLETED', 'CANCELED') THEN 'SAI FORMAT'
        ELSE 'OK'
    END AS note
FROM Orders;

PRINT '';
PRINT '========================================';
PRINT 'BƯỚC 2: XÓA TẤT CẢ KÝ TỰ LẠ';
PRINT '========================================';

-- Xóa dấu ngoặc kép, dấu nháy đơn, khoảng trắng thừa
UPDATE Orders
SET status = LTRIM(RTRIM(REPLACE(REPLACE(REPLACE(status, '"', ''), '''', ''), CHAR(9), '')));

PRINT 'Đã xóa ký tự lạ';

PRINT '';
PRINT '========================================';
PRINT 'BƯỚC 3: CHUẨN HÓA CHỮ HOA';
PRINT '========================================';

-- Chuyển tất cả sang chữ hoa
UPDATE Orders
SET status = UPPER(status);

PRINT 'Đã chuẩn hóa chữ hoa';

PRINT '';
PRINT '========================================';
PRINT 'BƯỚC 4: SỬA CÁC GIÁ TRỊ SAI';
PRINT '========================================';

-- Sửa tất cả giá trị không đúng về PENDING, SHIPPING, COMPLETED, CANCELED
UPDATE Orders
SET status = CASE 
    -- Pending
    WHEN status IN ('PENDING', 'CHO_XAC_NHAN', 'CHỜ XÁC NHẬN', 'CHO XAC NHAN', 'WAIT', 'WAITING') 
        THEN 'PENDING'
    
    -- Shipping
    WHEN status IN ('SHIPPING', 'DANG_GIAO', 'ĐANG GIAO', 'DANG GIAO', 'DELIVERING', 'SHIPPED') 
        THEN 'SHIPPING'
    
    -- Completed
    WHEN status IN ('COMPLETED', 'HOAN_THANH', 'HOÀN THÀNH', 'HOAN THANH', 'DONE', 'SUCCESS', 'FINISHED') 
        THEN 'COMPLETED'
    
    -- Canceled
    WHEN status IN ('CANCELED', 'CANCELLED', 'DA_HUY', 'ĐÃ HỦY', 'DA HUY', 'CANCEL') 
        THEN 'CANCELED'
    
    -- Mặc định
    ELSE 'PENDING'
END;

PRINT 'Đã sửa giá trị sai';

PRINT '';
PRINT '========================================';
PRINT 'BƯỚC 5: KIỂM TRA KẾT QUẢ';
PRINT '========================================';

-- Xem kết quả cuối cùng
SELECT 
    status, 
    COUNT(*) AS total,
    LEN(status) AS length
FROM Orders
GROUP BY status, LEN(status)
ORDER BY status;

-- Kiểm tra xem còn giá trị lạ không
DECLARE @badCount INT;
SELECT @badCount = COUNT(*)
FROM Orders
WHERE status NOT IN ('PENDING', 'SHIPPING', 'COMPLETED', 'CANCELED');

IF @badCount > 0
BEGIN
    PRINT '';
    PRINT '⚠️ CẢNH BÁO: Vẫn còn ' + CAST(@badCount AS VARCHAR) + ' đơn hàng có trạng thái lạ!';
    
    SELECT id, status, LEN(status) AS length
    FROM Orders
    WHERE status NOT IN ('PENDING', 'SHIPPING', 'COMPLETED', 'CANCELED');
END
ELSE
BEGIN
    PRINT '';
    PRINT '✅ HOÀN TẤT! Tất cả trạng thái đã đúng!';
END

PRINT '';
PRINT '========================================';
PRINT 'BƯỚC 6: TẠO ĐƠN HÀNG MẪU ĐỂ TEST';
PRINT '========================================';

-- Xóa đơn hàng cũ nếu có
DELETE FROM OrderDetails WHERE order_id IN (SELECT id FROM Orders WHERE account_id = 2);
DELETE FROM Orders WHERE account_id = 2;

-- Tạo 3 đơn hàng mẫu với 3 trạng thái khác nhau
INSERT INTO Orders (account_id, order_date, status, total_amount, receiver_name, receiver_phone, receiver_address)
VALUES 
(2, GETDATE(), 'PENDING', 500000, N'Nguyễn Văn A', '0911111111', N'Hồ Chí Minh'),
(2, GETDATE(), 'SHIPPING', 750000, N'Nguyễn Văn A', '0911111111', N'Hồ Chí Minh'),
(2, GETDATE(), 'COMPLETED', 1000000, N'Nguyễn Văn A', '0911111111', N'Hồ Chí Minh');

-- Lấy ID của đơn hàng vừa tạo
DECLARE @orderId1 INT, @orderId2 INT, @orderId3 INT;
SELECT TOP 1 @orderId1 = id FROM Orders WHERE status = 'PENDING' ORDER BY id DESC;
SELECT TOP 1 @orderId2 = id FROM Orders WHERE status = 'SHIPPING' ORDER BY id DESC;
SELECT TOP 1 @orderId3 = id FROM Orders WHERE status = 'COMPLETED' ORDER BY id DESC;

-- Thêm chi tiết đơn hàng
INSERT INTO OrderDetails (order_id, product_id, quantity, price, product_name)
VALUES 
(@orderId1, 1, 1, 500000, N'Áo thun Nam Basic Đen'),
(@orderId2, 2, 1, 750000, N'Áo sơ mi Nam Classic Trắng'),
(@orderId3, 3, 2, 500000, N'Quần jean Nam Slimfit Xanh navy');

PRINT 'Đã tạo 3 đơn hàng mẫu:';
PRINT '  - Đơn ' + CAST(@orderId1 AS VARCHAR) + ': PENDING';
PRINT '  - Đơn ' + CAST(@orderId2 AS VARCHAR) + ': SHIPPING';
PRINT '  - Đơn ' + CAST(@orderId3 AS VARCHAR) + ': COMPLETED';

PRINT '';
PRINT '========================================';
PRINT 'HƯỚNG DẪN TEST';
PRINT '========================================';
PRINT '1. Restart Spring Boot server';
PRINT '2. Đăng nhập admin: admin@example.com / 123456';
PRINT '3. Vào "Đơn hàng" → Chọn đơn PENDING';
PRINT '4. Nhấn "Cập nhật" → Đổi sang "Hoàn thành"';
PRINT '5. Nhấn "Lưu"';
PRINT '6. Kiểm tra: Trạng thái phải đổi ngay sang "Hoàn thành"';
PRINT '';
PRINT '✅ SCRIPT HOÀN TẤT!';
GO
