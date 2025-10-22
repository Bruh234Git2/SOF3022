# Cập nhật: Tính năng lọc sản phẩm theo giới tính

## Ngày cập nhật: 21/10/2025

---

## 🎯 Tính năng mới

Đã thêm chức năng **lọc sản phẩm theo giới tính** (Nam, Nữ, Unisex) vào hệ thống.

---

## 📝 Các thay đổi

### 1. **Database Schema**
Bảng `Products` đã có sẵn cột `gender`:
```sql
ALTER TABLE Products ADD gender NVARCHAR(10) NULL;
```

**Giá trị hợp lệ:**
- `'Nam'` - Sản phẩm dành cho nam
- `'Nữ'` - Sản phẩm dành cho nữ  
- `'Unisex'` - Sản phẩm trung tính (túi xách, phụ kiện)

### 2. **Entity Product**
File: `Product.java`

Field `gender` đã được khai báo:
```java
@Column(name = "gender", length = 10)
private String gender; // Nam | Nữ | Unisex
```

### 3. **ProductService**
File: `ProductService.java`

Đã thêm logic filter theo gender trong method `search()`:
```java
// Filter theo gender (Nam, Nữ, Unisex)
String gender = trim(params.get("gender"));
if(gender != null){
    ps.add(cb.equal(root.get("gender"), gender));
}
```

### 4. **Navbar**
File: `fragments/navbar.html`

Cập nhật links "Nam" và "Nữ" để truyền đúng parameter:
```html
<li class="nav-item">
  <a class="nav-link" th:href="@{/products(gender='Nam')}" title="Sản phẩm Nam">Nam</a>
</li>
<li class="nav-item">
  <a class="nav-link" th:href="@{/products(gender='Nữ')}" title="Sản phẩm Nữ">Nữ</a>
</li>
```

### 5. **Product List Page**
File: `pages/product-list.html`

Đã thêm dropdown filter giới tính:
```html
<select class="form-select form-select-sm" style="width:120px" name="gender">
  <option value="">Giới tính</option>
  <option value="Nam" th:selected="${params['gender']=='Nam'}">Nam</option>
  <option value="Nữ" th:selected="${params['gender']=='Nữ'}">Nữ</option>
  <option value="Unisex" th:selected="${params['gender']=='Unisex'}">Unisex</option>
</select>
```

Tất cả pagination links đã bao gồm parameter `gender` để giữ filter khi chuyển trang.

---

## 🚀 Cách sử dụng

### Từ Navbar
1. Click vào **"Nam"** trên navbar → Hiển thị tất cả sản phẩm nam
2. Click vào **"Nữ"** trên navbar → Hiển thị tất cả sản phẩm nữ

### Từ trang sản phẩm
1. Vào `/products`
2. Chọn giới tính từ dropdown "Giới tính"
3. Click nút **"Lọc"**
4. Kết hợp với các filter khác: danh mục, giá, giảm giá, sắp xếp

### URL Examples
```
/products?gender=Nam
/products?gender=Nữ
/products?gender=Unisex
/products?gender=Nam&category=1
/products?gender=Nữ&sale=1&sort=priceAsc
```

---

## 📊 Phân bố sản phẩm theo giới tính

Theo database script đã chạy:

| Danh mục | Nam | Nữ | Unisex | Tổng |
|----------|-----|-----|--------|------|
| Áo thun | 10 | 10 | 0 | 20 |
| Áo sơ mi | 10 | 10 | 0 | 20 |
| Quần jean | 10 | 10 | 0 | 20 |
| Quần tây | 10 | 10 | 0 | 20 |
| Giày thể thao | 10 | 10 | 0 | 20 |
| Giày da | 10 | 10 | 0 | 20 |
| Túi xách | 0 | 0 | 20 | 20 |
| Phụ kiện | 0 | 0 | 20 | 20 |
| Áo khoác | 10 | 10 | 0 | 20 |
| Váy đầm | 0 | 20 | 0 | 20 |
| **TỔNG** | **70** | **90** | **40** | **200** |

---

## 🔍 Query kiểm tra

Kiểm tra phân bố gender trong database:
```sql
SELECT category_id, gender, COUNT(*) AS total
FROM Products
GROUP BY category_id, gender
ORDER BY category_id, gender;
```

Xem sản phẩm theo gender:
```sql
-- Sản phẩm Nam
SELECT id, name, gender, category_id
FROM Products
WHERE gender = N'Nam'
ORDER BY category_id, id;

-- Sản phẩm Nữ
SELECT id, name, gender, category_id
FROM Products
WHERE gender = N'Nữ'
ORDER BY category_id, id;

-- Sản phẩm Unisex
SELECT id, name, gender, category_id
FROM Products
WHERE gender = N'Unisex'
ORDER BY category_id, id;
```

---

## ✅ Testing Checklist

- [x] Click "Nam" trên navbar → Hiển thị đúng sản phẩm nam
- [x] Click "Nữ" trên navbar → Hiển thị đúng sản phẩm nữ
- [x] Dropdown "Giới tính" trên trang sản phẩm hoạt động
- [x] Filter gender kết hợp với danh mục
- [x] Filter gender kết hợp với giá
- [x] Filter gender kết hợp với "Giảm giá"
- [x] Pagination giữ nguyên filter gender
- [x] Sort (sắp xếp) giữ nguyên filter gender
- [x] Search kết hợp với gender filter

---

## 🎨 UI/UX

### Navbar
- Link "Nam" và "Nữ" rõ ràng, dễ nhìn
- Hover effect theo Bootstrap default

### Product List
- Dropdown "Giới tính" nằm giữa "Danh mục" và "Giá"
- Width: 120px - vừa đủ hiển thị text
- Tự động select option đúng khi có filter

### Breadcrumb
- Hiển thị "Trang chủ / Ưu đãi" hoặc tương tự
- Không thay đổi khi filter gender

---

## 🐛 Troubleshooting

### Không lọc được sản phẩm
1. Kiểm tra database có cột `gender` chưa:
```sql
SELECT TOP 5 id, name, gender FROM Products;
```

2. Kiểm tra giá trị gender trong DB (phải là 'Nam', 'Nữ', 'Unisex'):
```sql
SELECT DISTINCT gender FROM Products;
```

3. Xem log SQL trong console khi filter:
```
spring.jpa.show-sql=true
```

### Pagination bị mất filter
- Đảm bảo tất cả pagination links có `gender=${params['gender']}`
- Kiểm tra file `product-list.html` dòng 87, 97, 104, 114, 119

### Filter không hoạt động
- Clear browser cache
- Restart application
- Kiểm tra ProductService.search() có logic filter gender

---

## 📚 Code Reference

### ProductService.search()
```java
public Page<Product> search(Map<String, String> params){
    int page = parseInt(params.get("page"), 0);
    int size = parseInt(params.get("size"), 12);
    String sort = params.getOrDefault("sort", "popular");
    Pageable pageable = PageRequest.of(page, size, toSort(sort));

    Specification<Product> spec = (root, query, cb) -> {
        List<Predicate> ps = new ArrayList<>();
        
        // ... other filters ...
        
        // Filter theo gender
        String gender = trim(params.get("gender"));
        if(gender != null){
            ps.add(cb.equal(root.get("gender"), gender));
        }
        
        return cb.and(ps.toArray(new Predicate[0]));
    };
    return productRepository.findAll(spec, pageable);
}
```

### Controller
ProductController không cần thay đổi vì đã dùng `@RequestParam Map<String,String> params`

---

## 🔄 Migration từ version cũ

Nếu database cũ chưa có cột `gender`:

1. **Thêm cột:**
```sql
ALTER TABLE Products ADD gender NVARCHAR(10) NULL;
```

2. **Cập nhật giá trị mặc định:**
```sql
-- Set tất cả về Unisex trước
UPDATE Products SET gender = N'Unisex';

-- Hoặc phân chia theo danh mục
UPDATE Products SET gender = N'Nam' 
WHERE category_id IN (1,2,3,4,5,6,9) AND id % 2 = 0;

UPDATE Products SET gender = N'Nữ' 
WHERE category_id IN (1,2,3,4,5,6,9) AND id % 2 = 1;

UPDATE Products SET gender = N'Nữ' 
WHERE category_id = 10; -- Váy đầm

UPDATE Products SET gender = N'Unisex' 
WHERE category_id IN (7,8); -- Túi xách, phụ kiện
```

3. **Restart application**

---

## 💡 Best Practices

1. **Luôn validate gender value:**
   - Chỉ chấp nhận: 'Nam', 'Nữ', 'Unisex'
   - Không case-sensitive trong filter

2. **Kết hợp filters:**
   - Gender + Category: Áo thun Nam
   - Gender + Sale: Sản phẩm Nữ đang giảm giá
   - Gender + Price range: Giày Nam từ 500k-1tr

3. **SEO friendly URLs:**
   - `/products?gender=Nam` rõ ràng
   - Có thể mở rộng thành `/products/nam` nếu cần

4. **Performance:**
   - Đánh index cho cột gender nếu cần:
   ```sql
   CREATE INDEX IX_Products_Gender ON Products(gender);
   ```

---

## 🎯 Future Enhancements

- [ ] Thêm filter "Tất cả" để xóa gender filter
- [ ] Hiển thị số lượng sản phẩm mỗi gender
- [ ] Thêm icon Nam/Nữ cho trực quan hơn
- [ ] Mobile responsive cho filter bar
- [ ] Remember last filter trong session/cookie

---

**Ngày cập nhật:** 21/10/2025  
**Trạng thái:** ✅ HOÀN THÀNH VÀ TESTED
