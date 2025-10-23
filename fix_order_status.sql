-- Script sửa lỗi trạng thái đơn hàng trong database
USE ShopOMG;
GO

-- 1. Kiểm tra các trạng thái hiện tại
SELECT DISTINCT status, LEN(status) AS length, 
       CASE 
           WHEN status LIKE '"%"' THEN 'CÓ DẤU NGOẶC KÉP'
           ELSE 'BÌNH THƯỜNG'
       END AS note
FROM Orders;

-- 2. Xóa dấu ngoặc kép nếu có
UPDATE Orders
SET status = REPLACE(REPLACE(status, '"', ''), '''', '')
WHERE status LIKE '%"%' OR status LIKE '%''%';

-- 3. Chuẩn hóa tất cả trạng thái về chữ HOA
UPDATE Orders
SET status = UPPER(LTRIM(RTRIM(status)));

-- 4. Sửa các trạng thái sai thành đúng
UPDATE Orders
SET status = CASE 
    WHEN status IN ('PENDING', 'CHO_XAC_NHAN', 'CHỜ XÁC NHẬN') THEN 'PENDING'
    WHEN status IN ('SHIPPING', 'DANG_GIAO', 'ĐANG GIAO') THEN 'SHIPPING'
    WHEN status IN ('COMPLETED', 'HOAN_THANH', 'HOÀN THÀNH', 'DONE') THEN 'COMPLETED'
    WHEN status IN ('CANCELED', 'DA_HUY', 'ĐÃ HỦY', 'CANCELLED') THEN 'CANCELED'
    ELSE 'PENDING' -- Mặc định là PENDING nếu không khớp
END
WHERE status NOT IN ('PENDING', 'SHIPPING', 'COMPLETED', 'CANCELED');

-- 5. Kiểm tra lại kết quả
SELECT status, COUNT(*) AS total
FROM Orders
GROUP BY status
ORDER BY status;

PRINT '✅ Đã sửa xong trạng thái đơn hàng!';
GO
