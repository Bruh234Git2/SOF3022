# 📦 Database ShopOMG - Hướng Dẫn Sử Dụng

## 📋 Tổng Quan

Database hoàn chỉnh cho hệ thống Shop thời trang với:
- ✅ 2 Roles (ADMIN, USER)
- ✅ 6 Accounts mẫu
- ✅ 10 Categories (Áo thun, Áo sơ mi, Quần jean, Quần tây, Giày thể thao, Giày da, Túi xách, Phụ kiện, Áo khoác, Váy đầm)
- ✅ 200 Products (20 sản phẩm mỗi danh mục)
- ✅ 600 Product Images (3 ảnh phụ mỗi sản phẩm)
- ✅ ~500 Product Reviews (2-4 review mỗi sản phẩm)
- ✅ Index tối ưu hiệu suất

## 🚀 Cách Sử Dụng

### Bước 1: Cài Đặt Database

1. Mở **SQL Server Management Studio (SSMS)**
2. Kết nối tới SQL Server
3. Mở file `ShopOMG_Database_Complete.sql`
4. Nhấn **Execute** (F5) để chạy toàn bộ script
5. Chờ khoảng 30-60 giây để hoàn tất

### Bước 2: Kiểm Tra

```sql
USE ShopOMG;
GO

-- Kiểm tra số lượng dữ liệu
SELECT 'Roles' AS TableName, COUNT(*) AS Total FROM Roles
UNION ALL
SELECT 'Accounts', COUNT(*) FROM Accounts
UNION ALL
SELECT 'Categories', COUNT(*) FROM Categories
UNION ALL
SELECT 'Products', COUNT(*) FROM Products
UNION ALL
SELECT 'ProductImages', COUNT(*) FROM ProductImages
UNION ALL
SELECT 'ProductReviews', COUNT(*) FROM ProductReviews;
```

Kết quả mong đợi:
- Roles: 2
- Accounts: 6
- Categories: 10
- Products: 200
- ProductImages: 600
- ProductReviews: ~500

### Bước 3: Cấu Hình Spring Boot

Cập nhật file `application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ShopOMG;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 🔐 Tài Khoản Đăng Nhập

### Admin
- **Email**: `admin@example.com`
- **Password**: `123456`
- **Quyền**: Quản lý toàn bộ hệ thống

### User Mẫu
| Email | Password | Tên |
|-------|----------|-----|
| user1@example.com | 123456 | Nguyễn Văn A |
| user2@example.com | 123456 | Trần Thị B |
| user3@example.com | 123456 | Lê Văn C |
| user4@example.com | 123456 | Phạm Thị D |
| user5@example.com | 123456 | Hoàng Văn E |

⚠️ **LƯU Ý**: Trong production, password phải được mã hóa bằng BCrypt!

## 📊 Cấu Trúc Database

### Bảng Chính

#### 1. Roles
- `id`: INT (Primary Key)
- `name`: VARCHAR(20) - ADMIN, USER

#### 2. Accounts
- `id`: INT (Primary Key)
- `username`: VARCHAR(50) (Unique)
- `password`: VARCHAR(255)
- `full_name`: NVARCHAR(100)
- `email`: VARCHAR(100) (Unique)
- `phone`: VARCHAR(20)
- `address`: NVARCHAR(255)
- `role_id`: INT (Foreign Key → Roles)
- `status`: VARCHAR(20) - ACTIVE/INACTIVE
- `avatar`: NVARCHAR(255)
- `birth_date`: DATE
- `reset_password_token`: NVARCHAR(255)
- `token_expiry_date`: DATETIME

#### 3. Categories
- `id`: INT (Primary Key)
- `name`: NVARCHAR(100)
- `description`: NVARCHAR(255)
- `image`: NVARCHAR(255)

#### 4. Products
- `id`: INT (Primary Key)
- `name`: NVARCHAR(200)
- `price`: DECIMAL(18,2)
- `discount`: DECIMAL(5,2)
- `image`: NVARCHAR(255)
- `description`: NVARCHAR(MAX)
- `category_id`: INT (Foreign Key → Categories)
- `gender`: NVARCHAR(10) - Nam/Nữ/Unisex
- `sku`: VARCHAR(50) - SKU001, SKU002...

#### 5. ProductImages
- `id`: INT (Primary Key)
- `product_id`: INT (Foreign Key → Products)
- `image_url`: NVARCHAR(255)

#### 6. ProductReviews
- `id`: INT (Primary Key)
- `product_id`: INT (Foreign Key → Products)
- `account_id`: INT (Foreign Key → Accounts)
- `rating`: INT (1-5)
- `comment`: NVARCHAR(500)
- `created_at`: DATETIME

#### 7. Carts
- `id`: INT (Primary Key)
- `account_id`: INT (Foreign Key → Accounts)
- `product_id`: INT (Foreign Key → Products)
- `quantity`: INT
- `created_at`: DATETIME

#### 8. Orders
- `id`: INT (Primary Key)
- `account_id`: INT (Foreign Key → Accounts)
- `order_date`: DATETIME
- `status`: VARCHAR(20) - PENDING/SHIPPING/COMPLETED/CANCELED
- `total_amount`: DECIMAL(18,2)
- `receiver_name`: NVARCHAR(100)
- `receiver_phone`: VARCHAR(20)
- `receiver_address`: NVARCHAR(255)

#### 9. OrderDetails
- `id`: INT (Primary Key)
- `order_id`: INT (Foreign Key → Orders)
- `product_id`: INT (Foreign Key → Products)
- `quantity`: INT
- `price`: DECIMAL(18,2)
- `product_name`: NVARCHAR(255)
- `color`: NVARCHAR(50)
- `size`: NVARCHAR(50)

#### 10. Reports
- `revenue_id`: INT (Primary Key)
- `category_id`: INT (Foreign Key → Categories)
- `total_sales`: DECIMAL(18,2)
- `total_quantity`: INT
- `min_price`: DECIMAL(18,2)
- `max_price`: DECIMAL(18,2)
- `avg_price`: DECIMAL(18,2)

## 🎨 Đặc Điểm Dữ Liệu

### Sản Phẩm
- **Giới tính**: 
  - Áo thun, Áo sơ mi, Quần jean, Quần tây, Giày: 10 Nam + 10 Nữ
  - Túi xách, Phụ kiện: Unisex
  - Váy đầm: Chỉ Nữ
  
- **Giá**: Dao động từ 100,000đ - 1,500,000đ tùy danh mục
- **Discount**: 0% - 50%
- **SKU**: Format SKU001, SKU002... SKU200

### Ảnh
- Tất cả ảnh lấy từ Pexels (ảnh thật, chất lượng cao)
- Mỗi sản phẩm có 1 ảnh chính + 3 ảnh phụ
- Ảnh phù hợp với từng danh mục

### Đánh Giá
- Mỗi sản phẩm có 2-4 review
- Rating: 3-5 sao
- Comment tiếng Việt thực tế

## 🔧 Bảo Trì

### Xóa Dữ Liệu Test
```sql
-- Xóa giỏ hàng
DELETE FROM Carts;

-- Xóa đơn hàng (cẩn thận!)
DELETE FROM OrderDetails;
DELETE FROM Orders;
```

### Reset Database
Chạy lại file `ShopOMG_Database_Complete.sql` để reset toàn bộ.

### Backup Database
```sql
BACKUP DATABASE ShopOMG
TO DISK = 'C:\Backup\ShopOMG.bak'
WITH FORMAT, MEDIANAME = 'ShopOMG_Backup';
```

## 📝 Lưu Ý Quan Trọng

1. **Password**: Hiện tại là plain text `123456`. Trong production phải mã hóa BCrypt!
2. **Index**: Đã tạo index tối ưu cho ProductImages và ProductReviews
3. **Foreign Key**: Tất cả quan hệ đã được thiết lập đúng
4. **Cascade**: OrderDetails sẽ tự động xóa khi xóa Order (nếu cấu hình trong JPA)
5. **Status**: Order status chỉ nhận 4 giá trị: PENDING, SHIPPING, COMPLETED, CANCELED

## 🆘 Troubleshooting

### Lỗi: Database đã tồn tại
Script tự động xóa database cũ. Nếu lỗi, chạy thủ công:
```sql
ALTER DATABASE ShopOMG SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE ShopOMG;
```

### Lỗi: Không kết nối được
Kiểm tra:
- SQL Server đã chạy chưa
- TCP/IP đã enable trong SQL Server Configuration Manager
- Port 1433 đã mở
- Username/Password đúng

### Lỗi: Thiếu dữ liệu
Chạy lại script từ đầu. Script đã được thiết kế để chạy nhiều lần an toàn.

## 📞 Hỗ Trợ

Nếu gặp vấn đề, kiểm tra:
1. File `ShopOMG_Database_Complete.sql` đã chạy thành công chưa
2. Tất cả bảng đã được tạo: `SELECT * FROM INFORMATION_SCHEMA.TABLES`
3. Dữ liệu đã được insert: Chạy query kiểm tra ở Bước 2

---

**Tạo bởi**: SOF3022 Team  
**Ngày**: 23/10/2025  
**Version**: 1.0
