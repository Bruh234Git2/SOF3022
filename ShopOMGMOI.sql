--------------------------------------------------------------------
-- DATABASE SHOPOMG - HOÀN CHỈNH
-- Tạo bởi: SOF3022 Team
-- Ngày: 23/10/2025
-- Mô tả: Database hoàn chỉnh cho hệ thống Shop thời trang
--------------------------------------------------------------------

--------------------------------------------------------------------
-- XÓA VÀ TẠO LẠI DATABASE
--------------------------------------------------------------------
IF DB_ID('ShopOMG') IS NOT NULL
BEGIN
    ALTER DATABASE ShopOMG SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ShopOMG;
END
GO

CREATE DATABASE ShopOMG;
GO

USE ShopOMG;
GO

--------------------------------------------------------------------
-- TẠO CÁC BẢNG
--------------------------------------------------------------------

-- Bảng Roles (Vai trò người dùng)
CREATE TABLE Roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(20) NOT NULL UNIQUE
);

-- Bảng Accounts (Tài khoản người dùng)
CREATE TABLE Accounts (
    id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address NVARCHAR(255),
    role_id INT FOREIGN KEY REFERENCES Roles(id),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    avatar NVARCHAR(255) NULL,
    birth_date DATE NULL,
    reset_password_token NVARCHAR(255) NULL,
    token_expiry_date DATETIME NULL
);

-- Bảng Categories (Danh mục sản phẩm)
CREATE TABLE Categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255),
    image NVARCHAR(255) NULL
);

-- Bảng Products (Sản phẩm)
CREATE TABLE Products (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(200) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0,
    image NVARCHAR(255),
    description NVARCHAR(MAX),
    category_id INT FOREIGN KEY REFERENCES Categories(id),
    gender NVARCHAR(10) NULL,
    sku VARCHAR(50) NULL
);

-- Bảng ProductImages (Ảnh phụ sản phẩm)
CREATE TABLE ProductImages (
    id INT PRIMARY KEY IDENTITY(1,1),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    image_url NVARCHAR(255) NOT NULL
);

-- Bảng ProductReviews (Đánh giá sản phẩm)
CREATE TABLE ProductReviews (
    id INT PRIMARY KEY IDENTITY(1,1),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    account_id INT FOREIGN KEY REFERENCES Accounts(id),
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE()
);

-- Bảng Carts (Giỏ hàng)
CREATE TABLE Carts (
    id INT PRIMARY KEY IDENTITY(1,1),
    account_id INT FOREIGN KEY REFERENCES Accounts(id),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    created_at DATETIME DEFAULT GETDATE()
);

-- Bảng Orders (Đơn hàng)
CREATE TABLE Orders (
    id INT PRIMARY KEY IDENTITY(1,1),
    account_id INT FOREIGN KEY REFERENCES Accounts(id),
    order_date DATETIME DEFAULT GETDATE(),
    status VARCHAR(20) DEFAULT 'PENDING',
    total_amount DECIMAL(18,2),
    receiver_name NVARCHAR(100),
    receiver_phone VARCHAR(20),
    receiver_address NVARCHAR(255)
);

-- Bảng OrderDetails (Chi tiết đơn hàng)
CREATE TABLE OrderDetails (
    id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT FOREIGN KEY REFERENCES Orders(id),
    product_id INT FOREIGN KEY REFERENCES Products(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(18,2) NOT NULL,
    product_name NVARCHAR(255) NULL,
    color NVARCHAR(50) NULL,
    size NVARCHAR(50) NULL
);

-- Bảng Reports (Báo cáo)
CREATE TABLE Reports (
    revenue_id INT PRIMARY KEY IDENTITY(1,1),
    category_id INT FOREIGN KEY REFERENCES Categories(id),
    total_sales DECIMAL(18,2),
    total_quantity INT,
    min_price DECIMAL(18,2),
    max_price DECIMAL(18,2),
    avg_price DECIMAL(18,2)
);
GO

--------------------------------------------------------------------
-- TẠO INDEX ĐỂ TỐI ƯU HIỆU SUẤT
--------------------------------------------------------------------

-- Index cho ProductImages
CREATE INDEX IX_ProductImages_Product 
    ON ProductImages(product_id)
    INCLUDE (image_url);

-- Index cho ProductReviews
CREATE INDEX IX_ProductReviews_Product 
    ON ProductReviews(product_id, created_at DESC)
    INCLUDE (rating, account_id, comment);

CREATE INDEX IX_ProductReviews_Account 
    ON ProductReviews(account_id);
GO

--------------------------------------------------------------------
-- NHẬP DỮ LIỆU MẪU
--------------------------------------------------------------------

-- 1. Roles
INSERT INTO Roles (name) VALUES ('ADMIN'), ('USER');

-- 2. Accounts (Password: 123456 - Cần mã hóa BCrypt trong thực tế)
INSERT INTO Accounts (username, password, full_name, email, phone, address, role_id) VALUES
('admin', '123456', N'Quản trị viên', 'admin@example.com', '0900000000', N'Hà Nội', 1),
('user1', '123456', N'Nguyễn Văn A', 'user1@example.com', '0911111111', N'Hồ Chí Minh', 2),
('user2', '123456', N'Trần Thị B', 'user2@example.com', '0922222222', N'Đà Nẵng', 2),
('user3', '123456', N'Lê Văn C', 'user3@example.com', '0933333333', N'Hải Phòng', 2),
('user4', '123456', N'Phạm Thị D', 'user4@example.com', '0944444444', N'Cần Thơ', 2),
('user5', '123456', N'Hoàng Văn E', 'user5@example.com', '0955555555', N'Nha Trang', 2);

-- 3. Categories
INSERT INTO Categories (name, description, image) VALUES
(N'Áo thun', N'Các loại áo thun nam nữ', 'https://images.pexels.com/photos/4040714/pexels-photo-4040714.jpeg'),
(N'Áo sơ mi', N'Áo sơ mi công sở, đi chơi', 'https://images.pexels.com/photos/1813504/pexels-photo-1813504.jpeg'),
(N'Quần jean', N'Quần jean nam nữ', 'https://images.pexels.com/photos/4040714/pexels-photo-4040714.jpeg'),
(N'Quần tây', N'Quần tây công sở', 'https://images.pexels.com/photos/3755694/pexels-photo-3755694.jpeg'),
(N'Giày thể thao', N'Các loại giày sneaker', 'https://images.pexels.com/photos/2929992/pexels-photo-2929992.jpeg'),
(N'Giày da', N'Giày da nam nữ', 'https://images.pexels.com/photos/1884584/pexels-photo-1884584.jpeg'),
(N'Túi xách', N'Túi xách thời trang', 'https://images.pexels.com/photos/4040714/pexels-photo-4040714.jpeg'),
(N'Phụ kiện', N'Phụ kiện như nón, thắt lưng', 'https://images.pexels.com/photos/6311579/pexels-photo-6311579.jpeg'),
(N'Áo khoác', N'Áo khoác các loại', 'https://images.pexels.com/photos/8454348/pexels-photo-8454348.jpeg'),
(N'Váy đầm', N'Đầm, váy nữ thời trang', 'https://images.pexels.com/photos/6311579/pexels-photo-6311579.jpeg');
GO

--------------------------------------------------------------------
-- TẠO 200 SẢN PHẨM (20 SẢN PHẨM MỖI DANH MỤC)
--------------------------------------------------------------------
DECLARE @cat INT = 1;
DECLARE @count INT = 1;

WHILE @cat <= 10
BEGIN
    DECLARE @i INT = 1;
    WHILE @i <= 20
    BEGIN
        INSERT INTO Products (name, price, discount, image, description, category_id)
        VALUES (
            N'Sản phẩm ' + CAST(@count AS NVARCHAR(10)) + N' - ' + 
                (SELECT name FROM Categories WHERE id = @cat),
            CAST(CASE 
                WHEN @cat = 1 THEN 200000 + RAND()*200000
                WHEN @cat = 2 THEN 250000 + RAND()*250000
                WHEN @cat = 3 THEN 300000 + RAND()*300000
                WHEN @cat = 4 THEN 300000 + RAND()*400000
                WHEN @cat = 5 THEN 800000 + RAND()*400000
                WHEN @cat = 6 THEN 700000 + RAND()*800000
                WHEN @cat = 7 THEN 500000 + RAND()*400000
                WHEN @cat = 8 THEN 100000 + RAND()*200000
                WHEN @cat = 9 THEN 400000 + RAND()*600000
                ELSE 500000 + RAND()*700000 END AS DECIMAL(18,2)),
            CAST(RAND()*50 AS DECIMAL(5,2)),
            N'https://images.pexels.com/photos/' + CAST(100000 + @count AS NVARCHAR(10)) + N'/pexels-photo.jpg',
            N'Mô tả chi tiết sản phẩm số ' + CAST(@count AS NVARCHAR(10)) + N' thuộc danh mục ' + 
                (SELECT name FROM Categories WHERE id = @cat),
            @cat
        );
        SET @i = @i + 1;
        SET @count = @count + 1;
    END
    SET @cat = @cat + 1;
END
GO

--------------------------------------------------------------------
-- CẬP NHẬT SKU CHO SẢN PHẨM
--------------------------------------------------------------------
UPDATE Products
SET sku = 'SKU' + FORMAT(id, '000')
WHERE sku IS NULL;
GO

--------------------------------------------------------------------
-- CẬP NHẬT ẢNH CHÍNH CHO SẢN PHẨM THEO DANH MỤC
--------------------------------------------------------------------
UPDATE Products
SET image = 
CASE category_id
    WHEN 1 THEN 'https://images.pexels.com/photos/10404259/pexels-photo-10404259.jpeg'
    WHEN 2 THEN 'https://images.pexels.com/photos/428340/pexels-photo-428340.jpeg'
    WHEN 3 THEN 'https://images.pexels.com/photos/2983464/pexels-photo-2983464.jpeg'
    WHEN 4 THEN 'https://images.pexels.com/photos/3755701/pexels-photo-3755701.jpeg'
    WHEN 5 THEN 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg'
    WHEN 6 THEN 'https://images.pexels.com/photos/186035/pexels-photo-186035.jpeg'
    WHEN 7 THEN 'https://images.pexels.com/photos/4040714/pexels-photo-4040714.jpeg'
    WHEN 8 THEN 'https://images.pexels.com/photos/6311579/pexels-photo-6311579.jpeg'
    WHEN 9 THEN 'https://images.pexels.com/photos/7679728/pexels-photo-7679728.jpeg'
    WHEN 10 THEN 'https://images.pexels.com/photos/994523/pexels-photo-994523.jpeg'
END;
GO

--------------------------------------------------------------------
-- CẬP NHẬT GIỚI TÍNH SẢN PHẨM (10 NAM, 10 NỮ MỖI LOẠI)
--------------------------------------------------------------------
DECLARE @cat INT = 1;

WHILE @cat <= 10
BEGIN
    -- 10 sản phẩm đầu: Nam
    UPDATE Products
    SET gender = N'Nam'
    WHERE category_id = @cat
      AND id IN (
          SELECT TOP 10 id 
          FROM Products 
          WHERE category_id = @cat 
          ORDER BY id ASC
      );

    -- 10 sản phẩm sau: Nữ
    UPDATE Products
    SET gender = N'Nữ'
    WHERE category_id = @cat
      AND id IN (
          SELECT TOP 10 id 
          FROM Products 
          WHERE category_id = @cat 
          ORDER BY id DESC
      );

    SET @cat = @cat + 1;
END
GO

-- Cập nhật Unisex cho Túi xách và Phụ kiện
UPDATE Products
SET gender = N'Unisex'
WHERE category_id IN (7, 8);

-- Váy đầm chỉ dành cho Nữ
UPDATE Products
SET gender = N'Nữ'
WHERE category_id = 10;
GO

--------------------------------------------------------------------
-- CẬP NHẬT TÊN SẢN PHẨM THEO GIỚI TÍNH VÀ STYLE
--------------------------------------------------------------------
DECLARE @colors TABLE(val NVARCHAR(40));
INSERT INTO @colors(val) VALUES
 (N'Đen'),(N'Trắng'),(N'Xanh navy'),(N'Xanh lá'),
 (N'Xám'),(N'Nâu'),(N'Be'),(N'Đỏ đô'),(N'Kem'),(N'Xanh dương');

DECLARE @styles TABLE(val NVARCHAR(60));
INSERT INTO @styles(val) VALUES
 (N'Basic'),(N'Classic'),(N'Slimfit'),(N'Oversize'),
 (N'Vintage'),(N'Smart'),(N'Street'),(N'Essential');

UPDATE p
SET p.name =
  CASE p.category_id
    WHEN 1 THEN N'Áo thun '   + ISNULL(p.gender, N'') + N' ' + s.val + N' ' + c.val
    WHEN 2 THEN N'Áo sơ mi '  + ISNULL(p.gender, N'') + N' ' + s.val + N' ' + c.val
    WHEN 3 THEN N'Quần jean ' + ISNULL(p.gender, N'') + N' ' + s.val + N' ' + c.val
    WHEN 4 THEN N'Quần tây '  + ISNULL(p.gender, N'') + N' ' + s.val + N' ' + c.val
    WHEN 5 THEN N'Giày thể thao ' + ISNULL(p.gender, N'') + N' ' + s.val + N' ' + c.val
    WHEN 6 THEN N'Giày da '       + ISNULL(p.gender, N'') + N' ' + s.val + N' ' + c.val
    WHEN 7 THEN N'Túi xách '      + s.val + N' ' + c.val
    WHEN 8 THEN N'Phụ kiện '      + s.val + N' ' + c.val
    WHEN 9 THEN N'Áo khoác '      + ISNULL(p.gender, N'') + N' ' + s.val + N' ' + c.val
    WHEN 10 THEN N'Đầm '          + s.val + N' ' + c.val
    ELSE p.name
  END
FROM Products p
CROSS APPLY (SELECT TOP 1 val FROM @styles  ORDER BY NEWID()) s
CROSS APPLY (SELECT TOP 1 val FROM @colors  ORDER BY NEWID()) c;
GO

--------------------------------------------------------------------
-- TẠO ẢNH PHỤ (3 ẢNH MỖI SẢN PHẨM)
--------------------------------------------------------------------
INSERT INTO ProductImages(product_id, image_url)
SELECT p.id, 
CASE p.category_id
    WHEN 1 THEN 'https://images.pexels.com/photos/10026491/pexels-photo-10026491.jpeg'
    WHEN 2 THEN 'https://images.pexels.com/photos/1813504/pexels-photo-1813504.jpeg'
    WHEN 3 THEN 'https://images.pexels.com/photos/6625911/pexels-photo-6625911.jpeg'
    WHEN 4 THEN 'https://images.pexels.com/photos/3755694/pexels-photo-3755694.jpeg'
    WHEN 5 THEN 'https://images.pexels.com/photos/2929992/pexels-photo-2929992.jpeg'
    WHEN 6 THEN 'https://images.pexels.com/photos/1884584/pexels-photo-1884584.jpeg'
    WHEN 7 THEN 'https://images.pexels.com/photos/2929992/pexels-photo-2929992.jpeg'
    WHEN 8 THEN 'https://images.pexels.com/photos/276517/pexels-photo-276517.jpeg'
    WHEN 9 THEN 'https://images.pexels.com/photos/7679728/pexels-photo-7679728.jpeg'
    WHEN 10 THEN 'https://images.pexels.com/photos/6311579/pexels-photo-6311579.jpeg'
END
FROM Products p;

INSERT INTO ProductImages(product_id, image_url)
SELECT p.id, 
CASE p.category_id
    WHEN 1 THEN 'https://images.pexels.com/photos/6311578/pexels-photo-6311578.jpeg'
    WHEN 2 THEN 'https://images.pexels.com/photos/1043474/pexels-photo-1043474.jpeg'
    WHEN 3 THEN 'https://images.pexels.com/photos/1604992/pexels-photo-1604992.jpeg'
    WHEN 4 THEN 'https://images.pexels.com/photos/428338/pexels-photo-428338.jpeg'
    WHEN 5 THEN 'https://images.pexels.com/photos/2929991/pexels-photo-2929991.jpeg'
    WHEN 6 THEN 'https://images.pexels.com/photos/19090/pexels-photo.jpg'
    WHEN 7 THEN 'https://images.pexels.com/photos/2929992/pexels-photo-2929992.jpeg'
    WHEN 8 THEN 'https://images.pexels.com/photos/4040714/pexels-photo-4040714.jpeg'
    WHEN 9 THEN 'https://images.pexels.com/photos/6311577/pexels-photo-6311577.jpeg'
    WHEN 10 THEN 'https://images.pexels.com/photos/1027130/pexels-photo-1027130.jpeg'
END
FROM Products p;

INSERT INTO ProductImages(product_id, image_url)
SELECT p.id, 
CASE p.category_id
    WHEN 1 THEN 'https://images.pexels.com/photos/6311576/pexels-photo-6311576.jpeg'
    WHEN 2 THEN 'https://images.pexels.com/photos/2929989/pexels-photo-2929989.jpeg'
    WHEN 3 THEN 'https://images.pexels.com/photos/3755700/pexels-photo-3755700.jpeg'
    WHEN 4 THEN 'https://images.pexels.com/photos/3755693/pexels-photo-3755693.jpeg'
    WHEN 5 THEN 'https://images.pexels.com/photos/325876/pexels-photo-325876.jpeg'
    WHEN 6 THEN 'https://images.pexels.com/photos/3755692/pexels-photo-3755692.jpeg'
    WHEN 7 THEN 'https://images.pexels.com/photos/2929992/pexels-photo-2929992.jpeg'
    WHEN 8 THEN 'https://images.pexels.com/photos/276518/pexels-photo-276518.jpeg'
    WHEN 9 THEN 'https://images.pexels.com/photos/8454348/pexels-photo-8454348.jpeg'
    WHEN 10 THEN 'https://images.pexels.com/photos/267568/pexels-photo-267568.jpeg'
END
FROM Products p;
GO

--------------------------------------------------------------------
-- TẠO ĐÁNH GIÁ SẢN PHẨM (2-4 REVIEW MỖI SẢN PHẨM)
--------------------------------------------------------------------
DECLARE @prod INT = 1;
DECLARE @rev INT;

WHILE @prod <= 200
BEGIN
    SET @rev = 2 + ABS(CHECKSUM(NEWID())) % 3;
    DECLARE @r INT = 1;
    WHILE @r <= @rev
    BEGIN
        INSERT INTO ProductReviews (product_id, account_id, rating, comment, created_at)
        VALUES (
            @prod,
            2 + ABS(CHECKSUM(NEWID())) % 5,
            3 + ABS(CHECKSUM(NEWID())) % 3,
            (SELECT TOP 1 comment FROM (VALUES
                (N'Sản phẩm đẹp, đúng mô tả. Giao hàng nhanh.'),
                (N'Chất lượng tốt, đáng tiền.'),
                (N'Form vừa vặn, mặc thoải mái.'),
                (N'Giá hợp lý, đóng gói cẩn thận.'),
                (N'Rất thích, tặng thêm quà nhỏ.')) AS t(comment)),
            GETDATE()
        );
        SET @r = @r + 1;
    END
    SET @prod = @prod + 1;
END
GO

--------------------------------------------------------------------
-- HOÀN TẤT
--------------------------------------------------------------------
PRINT '✅ Đã tạo database ShopOMG hoàn chỉnh!';
PRINT '📊 Tổng quan:';
PRINT '   - 2 Roles (ADMIN, USER)';
PRINT '   - 6 Accounts';
PRINT '   - 10 Categories';
PRINT '   - 200 Products';
PRINT '   - 600 Product Images (3 ảnh/sản phẩm)';
PRINT '   - ~500 Product Reviews';
PRINT '';
PRINT '🔐 Tài khoản đăng nhập:';
PRINT '   Admin: admin@example.com / 123456';
PRINT '   User:  user1@example.com / 123456';
PRINT '';
PRINT '⚠️ LƯU Ý: Password cần mã hóa BCrypt trong production!';
GO


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

select * from Products
