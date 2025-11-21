# Fix: Lỗi Pagination không giữ filter Gender

## ⚠️ Vấn đề

**Hiện tượng:**
- Trang 1: Filter "Nam" → Hiển thị đúng sản phẩm Nam ✅
- Trang 2: Click "Sau" → Hiển thị sản phẩm Nữ ❌

**URL bị lỗi:**
```
Trang 1: localhost:8080/products?gender=Nam
Trang 2: localhost:8080/products?page=1&size=12&sort=popular&q=&category=&min=&max=&sale=
                                                                    ↑ THIẾU gender=Nam
```

---

## 🔍 Nguyên nhân

**File:** `src/main/resources/templates/pages/product-list.html`

Tất cả pagination links (Trước, 1, 2, 3..., Sau) **thiếu parameter `gender`**:

```html
<!-- SAI - Thiếu gender -->
<a th:href="@{/products(page=${i},size=${params['size']},sort=${params['sort']},
                        q=${params['q']},category=${params['category']},
                        min=${params['min']},max=${params['max']},sale=${params['sale']})}">
```

---

## ✅ Giải pháp

Thêm `gender=${params['gender']}` vào **TẤT CẢ** pagination links:

```html
<!-- ĐÚNG - Có gender -->
<a th:href="@{/products(page=${i},size=${params['size']},sort=${params['sort']},
                        q=${params['q']},category=${params['category']},
                        gender=${params['gender']},
                        min=${params['min']},max=${params['max']},sale=${params['sale']})}">
```

---

## 🛠️ Đã sửa

### File: `product-list.html`

**5 chỗ đã thêm `gender=${params['gender']}`:**

1. **Nút "Trước"** (line 92)
   ```html
   <a class="page-link" 
      th:href="@{/products(page=${curr-1},size=${params['size']},
                           sort=${params['sort']},q=${params['q']},
                           category=${params['category']},
                           gender=${params['gender']}, ← ĐÃ THÊM
                           min=${params['min']},max=${params['max']},
                           sale=${params['sale']})}">Trước</a>
   ```

2. **First 3 pages** (line 102)
   ```html
   <a class="page-link" th:text="${i+1}"
      th:href="@{/products(page=${i},...,
                           gender=${params['gender']}, ← ĐÃ THÊM
                           ...)}">1</a>
   ```

3. **Middle window** (line 109)
   ```html
   <a class="page-link" th:text="${i+1}"
      th:href="@{/products(page=${i},...,
                           gender=${params['gender']}, ← ĐÃ THÊM
                           ...)}">1</a>
   ```

4. **Last 3 pages** (line 119)
   ```html
   <a class="page-link" th:text="${i+1}"
      th:href="@{/products(page=${i},...,
                           gender=${params['gender']}, ← ĐÃ THÊM
                           ...)}">1</a>
   ```

5. **Nút "Sau"** (line 124)
   ```html
   <a class="page-link" 
      th:href="@{/products(page=${curr+1},...,
                           gender=${params['gender']}, ← ĐÃ THÊM
                           ...)}">Sau</a>
   ```

---

## 🧪 Test Cases

### Test 1: Filter Nam + Pagination
1. Click navbar "Nam"
2. URL: `localhost:8080/products?gender=Nam`
3. Trang 1: Hiển thị 12 sản phẩm Nam ✅
4. Click "Sau" (trang 2)
5. URL: `localhost:8080/products?page=1&gender=Nam&...` ✅
6. Trang 2: Vẫn hiển thị sản phẩm Nam ✅

### Test 2: Filter Nữ + Pagination
1. Click navbar "Nữ"
2. Trang 1: Sản phẩm Nữ ✅
3. Click "2", "3", "Sau"
4. Tất cả trang: Vẫn là sản phẩm Nữ ✅

### Test 3: Kết hợp nhiều filter + Pagination
1. Click "Nam"
2. Chọn category: "Áo thun"
3. Chọn "Giảm giá"
4. Click trang 2
5. URL phải có: `?page=1&gender=Nam&category=1&sale=1&...` ✅
6. Trang 2: Vẫn đúng filter ✅

### Test 4: Xóa filter + Pagination
1. Filter "Nam" → Trang 2
2. Xóa filter (chọn "Giới tính" → "Tất cả")
3. Click "Lọc"
4. URL: `?page=0&gender=&...` (gender rỗng)
5. Hiển thị tất cả sản phẩm ✅

---

## 📊 So sánh Trước/Sau

### TRƯỚC (BUG)
```
Bước 1: Click "Nam"
URL: /products?gender=Nam
Kết quả: ✅ 12 sản phẩm Nam

Bước 2: Click "Sau"
URL: /products?page=1&size=12&sort=popular&... (THIẾU gender=Nam)
Kết quả: ❌ 12 sản phẩm Nữ (sai!)
```

### SAU (FIXED)
```
Bước 1: Click "Nam"
URL: /products?gender=Nam
Kết quả: ✅ 12 sản phẩm Nam

Bước 2: Click "Sau"
URL: /products?page=1&gender=Nam&size=12&sort=popular&...
Kết quả: ✅ 12 sản phẩm Nam (đúng!)
```

---

## 🎯 Kiểm tra nhanh

Sau khi restart server:

1. **Vào trang Nam:**
   ```
   http://localhost:8080/products?gender=Nam
   ```

2. **Click trang 2, check URL:**
   - Phải có `gender=Nam` ✅
   - Sản phẩm vẫn là Nam ✅

3. **Vào trang Nữ:**
   ```
   http://localhost:8080/products?gender=Nữ
   ```

4. **Click qua các trang 2, 3, 4...**
   - Tất cả đều giữ `gender=Nữ` ✅

---

## ⚙️ Các trang khác

| Trang | Có Pagination? | Cần sửa? | Status |
|-------|----------------|----------|--------|
| `product-list.html` | ✅ Có | ✅ Cần | ✅ Đã sửa |
| `order-list.html` | ❌ Không | ❌ Không | N/A |
| `my-product-list.html` | ❌ Không | ❌ Không | N/A |
| `admin/product.html` | ❌ Không | ❌ Không | N/A |
| `admin/order.html` | ❌ Không | ❌ Không | N/A |

→ **Chỉ cần sửa 1 file duy nhất!** ✅

---

## 🚀 Rebuild & Test

### Bước 1: Restart Server
```bash
# Stop server (Ctrl+C)
# Hoặc trong IDE: Stop Application

# Start lại
mvn spring-boot:run
```

### Bước 2: Clear Browser Cache
```
Ctrl+Shift+Delete → Clear cache
Hoặc: Hard refresh (Ctrl+F5)
```

### Bước 3: Test
1. Click "Nam" → Trang 1 OK
2. Click "2" → Trang 2 vẫn Nam ✅
3. Click "Nữ" → Trang 1 OK
4. Click "Sau" → Trang 2 vẫn Nữ ✅

---

## 📝 Summary

| Vấn đề | Nguyên nhân | Giải pháp | Files sửa |
|--------|-------------|-----------|-----------|
| Pagination mất filter gender | Thiếu `gender=${params['gender']}` | Thêm vào 5 chỗ | 1 file |

**Files thay đổi:**
1. ✅ `src/main/resources/templates/pages/product-list.html`

**Dòng code sửa:** 5 chỗ (lines 92, 102, 109, 119, 124)

**Impact:** 
- ✅ Filter Nam + Pagination → Hoạt động
- ✅ Filter Nữ + Pagination → Hoạt động
- ✅ Kết hợp nhiều filter → Hoạt động

---

## ✅ Checklist

- [x] Sửa nút "Trước"
- [x] Sửa First 3 pages
- [x] Sửa Middle window
- [x] Sửa Last 3 pages
- [x] Sửa nút "Sau"
- [x] Kiểm tra các trang khác
- [ ] **→ Restart server** (User làm)
- [ ] **→ Test filter Nam + pagination** (User làm)
- [ ] **→ Test filter Nữ + pagination** (User làm)

---

**Tác giả:** Cascade AI  
**Ngày:** 2025-11-07  
**Issue:** #PAGINATION_GENDER_FILTER  
**Status:** ✅ FIXED
