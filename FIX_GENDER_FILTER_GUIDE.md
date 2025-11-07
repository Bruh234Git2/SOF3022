# Hướng dẫn: Sửa lỗi Filter Giới tính

## ⚠️ Vấn đề đã phát hiện

Khi filter sản phẩm theo giới tính "Nam", kết quả hiển thị cả sản phẩm "Đầm" (dành cho Nữ).

**Nguyên nhân:**
- Product entity thiếu field `gender`
- Filter gender trong ProductService đã bị comment out
- Cart table thiếu cột `color` và `size`

---

## ✅ Đã sửa

### 1. **Product Entity** (Product.java)
Đã thêm field:
```java
@Column(name = "gender", length = 10)
private String gender; // Nam, Nữ, Unisex
```

### 2. **ProductService** (ProductService.java)
Đã enable filter:
```java
// Filter theo gender (Nam, Nữ, Unisex)
String gender = trim(params.get("gender"));
if(gender != null){
    ps.add(cb.equal(root.get("gender"), gender));
}
```

### 3. **ProductDTO** (ProductDTO.java)
Đã thêm fields:
```java
private String sku;
private String gender; // Nam, Nữ, Unisex
```

### 4. **Cart Migration Script**
Tạo file: `DATABASE_MIGRATION_ADD_CART_COLUMNS.sql`
- Thêm cột `color NVARCHAR(50)` vào Carts table
- Thêm cột `size NVARCHAR(50)` vào Carts table

---

## 🔧 Các bước thực hiện

### Bước 1: Chạy Database Script gốc
```sql
-- Chạy file ShopOMG_Database_Complete.sql
-- Script này tạo database và 200 sản phẩm
```

**Kết quả:**
- 200 Products với gender được set:
  - 100 sản phẩm Nam (categories 1-9, 10 sản phẩm đầu mỗi loại)
  - 80 sản phẩm Nữ (categories 1-9, 10 sản phẩm sau mỗi loại + toàn bộ category 10)
  - 20 sản phẩm Unisex (categories 7, 8 - Túi xách và Phụ kiện)

### Bước 2: Chạy Migration Script
```sql
-- Chạy file DATABASE_MIGRATION_ADD_CART_COLUMNS.sql
-- Script này thêm color và size vào Carts table
```

### Bước 3: Rebuild Spring Boot Project
```bash
# Trong terminal tại thư mục project
mvn clean install

# Hoặc trong IDE
Right-click project → Maven → Update Project → Force Update
```

### Bước 4: Restart Server
```bash
mvn spring-boot:run

# Hoặc trong IDE
Stop server → Run Application.java
```

---

## 🧪 Test Cases

### Test 1: Filter Nam
1. Truy cập: `http://localhost:8080/products?gender=Nam`
2. Click vào navbar: **"Nam"**
3. **Kết quả mong đợi:**
   - Chỉ hiển thị sản phẩm có gender = 'Nam'
   - Không có sản phẩm "Đầm", "Váy"
   - Dropdown filter "Giới tính" hiển thị "Nam"

### Test 2: Filter Nữ
1. Click navbar: **"Nữ"**
2. **Kết quả mong đợi:**
   - Chỉ hiển thị sản phẩm có gender = 'Nữ'
   - Bao gồm: Áo, Quần, Giày, Đầm/Váy cho nữ

### Test 3: Kết hợp Filter
1. Click "Nam"
2. Chọn category: "Áo thun"
3. **Kết quả mong đợi:**
   - Chỉ hiển thị áo thun Nam (10 sản phẩm)

### Test 4: Khuyến mãi
1. Click navbar: **"Khuyến mãi"**
2. **Kết quả mong đợi:**
   - Hiển thị tất cả sản phẩm có discount > 0
   - Bao gồm cả Nam, Nữ, Unisex

### Test 5: Add to Cart với Color/Size
1. Vào chi tiết sản phẩm
2. Chọn màu: "Trắng"
3. Chọn size: "M"
4. Click "Thêm vào giỏ"
5. Vào giỏ hàng
6. **Kết quả mong đợi:**
   - Sản phẩm hiển thị với color và size đã chọn

---

## 🔍 Debug Queries

### Kiểm tra gender trong database
```sql
-- Xem phân bố gender
SELECT gender, COUNT(*) AS total
FROM Products
GROUP BY gender
ORDER BY gender;

-- Kết quả mong đợi:
-- Nam: 100
-- Nữ: 80
-- Unisex: 20
```

### Kiểm tra Carts table structure
```sql
-- Xem cấu trúc bảng
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Carts'
ORDER BY ORDINAL_POSITION;

-- Phải có: id, account_id, product_id, quantity, color, size, created_at
```

### Test filter query
```sql
-- Lấy 10 sản phẩm Nam
SELECT TOP 10 id, name, gender, price
FROM Products
WHERE gender = N'Nam'
ORDER BY id;
```

---

## 📊 Cấu trúc Gender theo Database

Theo script `ShopOMG_Database_Complete.sql`:

| Category | ID | Tổng SP | Nam | Nữ | Unisex | Ghi chú |
|----------|----|---------|----|----|----|---------|
| Áo thun | 1 | 20 | 10 | 10 | 0 | - |
| Áo sơ mi | 2 | 20 | 10 | 10 | 0 | - |
| Quần jean | 3 | 20 | 10 | 10 | 0 | - |
| Quần tây | 4 | 20 | 10 | 10 | 0 | - |
| Giày thể thao | 5 | 20 | 10 | 10 | 0 | - |
| Giày da | 6 | 20 | 10 | 10 | 0 | - |
| Túi xách | 7 | 20 | 0 | 0 | 20 | Unisex |
| Phụ kiện | 8 | 20 | 0 | 0 | 20 | Unisex |
| Áo khoác | 9 | 20 | 10 | 10 | 0 | - |
| Váy đầm | 10 | 20 | 0 | 20 | 0 | Chỉ Nữ |
| **TỔNG** | - | **200** | **100** | **80** | **20** | - |

---

## ⚠️ Lưu ý quan trọng

### 1. Gender trong Database
Giá trị gender phải **CHÍNH XÁC** theo format:
- ✅ `'Nam'` (có dấu N viết hoa)
- ✅ `'Nữ'` (có dấu N viết hoa)  
- ✅ `'Unisex'` (U viết hoa)
- ❌ Không được: `'nam'`, `'nữ'`, `'UNISEX'`

### 2. Navbar Links
Kiểm tra file: `src/main/resources/templates/fragments/navbar.html`
```html
<li class="nav-item">
  <a class="nav-link" th:href="@{/products(gender='Nam')}">Nam</a>
</li>
<li class="nav-item">
  <a class="nav-link" th:href="@{/products(gender='Nữ')}">Nữ</a>
</li>
```

### 3. ProductList Filter Dropdown
File: `src/main/resources/templates/pages/product-list.html`
```html
<select class="form-select form-select-sm" name="gender">
  <option value="">Giới tính</option>
  <option value="Nam" th:selected="${params['gender']=='Nam'}">Nam</option>
  <option value="Nữ" th:selected="${params['gender']=='Nữ'}">Nữ</option>
  <option value="Unisex" th:selected="${params['gender']=='Unisex'}">Unisex</option>
</select>
```

### 4. Cart Color/Size
Sau khi thêm color/size vào database:
- Các cart items cũ sẽ có color = NULL, size = NULL
- Các cart items mới sẽ có color và size từ form

---

## 🐛 Troubleshooting

### Vấn đề 1: Filter vẫn không hoạt động
**Kiểm tra:**
```sql
-- Xem gender của 1 sản phẩm cụ thể
SELECT id, name, gender, LEN(gender) AS length
FROM Products
WHERE id = 1;

-- Nếu length khác 3 (Nam) hoặc 3 (Nữ) hoặc 6 (Unisex) → Có ký tự lạ
```

**Giải pháp:**
```sql
-- Chuẩn hóa lại gender
UPDATE Products
SET gender = LTRIM(RTRIM(gender));
```

### Vấn đề 2: Server không khởi động
**Lỗi:** `Column 'gender' not found`

**Giải pháp:**
1. Kiểm tra database có cột gender chưa:
```sql
SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Products' AND COLUMN_NAME = 'gender';
```

2. Nếu không có → Chạy lại database script

### Vấn đề 3: Cart không lưu color/size
**Lỗi:** `Column 'color' not found in Carts`

**Giải pháp:**
Chạy migration script: `DATABASE_MIGRATION_ADD_CART_COLUMNS.sql`

---

## 📁 Files đã thay đổi

### Backend
1. ✅ `src/main/java/poly/edu/entity/Product.java` - Thêm gender field
2. ✅ `src/main/java/poly/edu/dto/ProductDTO.java` - Thêm sku, gender
3. ✅ `src/main/java/poly/edu/service/ProductService.java` - Enable gender filter

### Database
4. ✅ `DATABASE_MIGRATION_ADD_CART_COLUMNS.sql` - **MỚI** - Add color/size to Carts

### Documentation
5. ✅ `FIX_GENDER_FILTER_GUIDE.md` - **MỚI** - Hướng dẫn này

---

## 🎯 Checklist hoàn thành

- [x] Thêm gender field vào Product entity
- [x] Enable gender filter trong ProductService
- [x] Cập nhật ProductDTO
- [x] Cập nhật convertToDTO, createProduct, updateProduct
- [x] Tạo migration script cho Cart color/size
- [x] Tạo document hướng dẫn chi tiết
- [ ] **→ Chạy database script** (User làm)
- [ ] **→ Chạy migration script** (User làm)
- [ ] **→ Rebuild & restart server** (User làm)
- [ ] **→ Test filter Nam/Nữ** (User làm)

---

## 📞 Next Steps

1. **Chạy database script gốc:**
   - File: `ShopOMG_Database_Complete.sql`
   - Script đã set gender cho 200 sản phẩm

2. **Chạy migration script:**
   - File: `DATABASE_MIGRATION_ADD_CART_COLUMNS.sql`
   - Thêm color và size vào Carts table

3. **Rebuild project:**
   ```bash
   mvn clean install
   ```

4. **Restart server và test:**
   - Click "Nam" trên navbar
   - Xác nhận chỉ hiển thị sản phẩm Nam
   - Click "Nữ" → chỉ sản phẩm Nữ

---

**Tác giả:** Cascade AI  
**Ngày:** 2025-11-07  
**Version:** 2.0  
**Project:** SOF3022 - Shop OMG!
