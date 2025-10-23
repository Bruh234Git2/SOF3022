# Hướng Dẫn Sửa Lỗi Đơn Hàng

## 🐛 Các Lỗi Đã Sửa

### 1. Lỗi Template Parsing (order-detail.html)
**Lỗi**: `#strings.padLeft()` không tồn tại trong Thymeleaf  
**Sửa**: Đổi thành hiển thị ID đơn giản `'Đơn hàng #ORD-' + ${order.id}`

### 2. Lỗi Trạng Thái Không Cập Nhật
**Nguyên nhân**: Database có thể chứa dữ liệu cũ với status sai format  
**Giải pháp**: Chạy script SQL để fix

## 📋 Các Bước Thực Hiện

### Bước 1: Chạy Script SQL Fix Database
1. Mở SQL Server Management Studio
2. Kết nối tới database `ShopOMG`
3. Mở file `fix_order_status.sql`
4. Chạy toàn bộ script
5. Kiểm tra kết quả:
   ```sql
   SELECT status, COUNT(*) AS total
   FROM Orders
   GROUP BY status;
   ```
   Kết quả phải chỉ có: `PENDING`, `SHIPPING`, `COMPLETED`, `CANCELED`

### Bước 2: Restart Server
1. Dừng Spring Boot application (Ctrl+C hoặc Stop trong IDE)
2. Xóa cache nếu có
3. Khởi động lại server

### Bước 3: Test Lại Chức Năng

#### Test 1: Cập nhật trạng thái admin
1. Đăng nhập admin: `admin@example.com` / `123456`
2. Vào "Đơn hàng"
3. Nhấn "Cập nhật" một đơn hàng
4. Đổi trạng thái sang "Hoàn thành"
5. Nhấn "Lưu"
6. ✅ Kiểm tra: Trạng thái hiển thị "Hoàn thành" ngay lập tức

#### Test 2: Sản phẩm đã mua
1. Đăng nhập user: `user1@example.com` / `123456`
2. Admin đổi đơn hàng của user này sang "Hoàn thành"
3. User vào "Sản phẩm đã mua"
4. ✅ Kiểm tra: Sản phẩm hiển thị đầy đủ

#### Test 3: Hiển thị trạng thái
1. Vào "Đơn hàng đã đặt"
2. ✅ Kiểm tra: Hiển thị "Chờ xác nhận" thay vì "PENDING"
3. Nhấn "Xem" chi tiết
4. ✅ Kiểm tra: Badge hiển thị đúng màu và text

## 🔍 Debug Nếu Vẫn Lỗi

### Kiểm tra Database
```sql
-- Xem trạng thái hiện tại
SELECT id, status, LEN(status) AS length
FROM Orders
WHERE id = 1; -- Thay 1 bằng ID đơn hàng bạn đang test

-- Nếu thấy length > 10, có thể có ký tự thừa
-- Chạy lại script fix_order_status.sql
```

### Kiểm tra Backend Log
Xem console khi nhấn "Lưu" trong admin:
- Phải thấy request PUT tới `/admin/api/orders/{id}/status`
- Response phải là 200 OK
- Không có exception

### Kiểm tra Browser Console
1. Mở DevTools (F12)
2. Tab Network
3. Nhấn "Lưu"
4. Xem request:
   - Method: PUT
   - URL: `/admin/api/orders/{id}/status`
   - Payload: `"COMPLETED"` (có dấu ngoặc kép là đúng, backend sẽ xử lý)
   - Response: Phải có `"status":"COMPLETED"`

## 📝 Các File Đã Sửa

1. ✅ `order-detail.html` - Sửa lỗi Thymeleaf
2. ✅ `fix_order_status.sql` - Script fix database
3. ✅ `AdminController.java` - Đã có sẵn logic xử lý dấu ngoặc kép
4. ✅ `PageController.java` - Đã sửa query chỉ tìm COMPLETED
5. ✅ `order-list.html` - Đã sửa hiển thị tiếng Việt
6. ✅ `admin/order.html` - Đã sửa await response

## 🎯 Checklist Cuối Cùng

- [ ] Chạy script `fix_order_status.sql`
- [ ] Restart server
- [ ] Test cập nhật trạng thái admin → Thành công
- [ ] Test sản phẩm đã mua → Hiển thị đúng
- [ ] Test hiển thị trạng thái → Tiếng Việt đúng
- [ ] Không có lỗi trong console

## 💡 Lưu Ý Quan Trọng

1. **Trạng thái trong DB luôn là chữ HOA**: `PENDING`, `SHIPPING`, `COMPLETED`, `CANCELED`
2. **Frontend gửi lowercase** → Backend tự convert sang uppercase
3. **Thymeleaf hiển thị** → Dùng ternary operator để chuyển sang tiếng Việt
4. **Admin modal** → Dropdown có value lowercase, nhưng gửi uppercase khi save

## 🆘 Nếu Vẫn Không Được

Hãy gửi cho tôi:
1. Screenshot lỗi trong browser console (F12)
2. Kết quả query: `SELECT TOP 5 * FROM Orders`
3. Log từ Spring Boot console khi nhấn "Lưu"
