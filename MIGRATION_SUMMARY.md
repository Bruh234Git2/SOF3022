# Tóm tắt Migration: Chuyển JavaScript sang Backend

## Ngày thực hiện
2025-11-07

## Tổng quan
Đã chuyển các chức năng JavaScript trong `product-detail.html` và `product-list.html` sang backend sử dụng Spring Boot Controllers và Services.

---

## 1. Thay đổi Backend

### 1.1. Tạo GlobalControllerAdvice
**File mới:** `src/main/java/poly/edu/config/GlobalControllerAdvice.java`

**Mục đích:**
- Tự động inject `cartCount` vào tất cả views
- Inject `currentAccount` cho navbar
- Không cần JavaScript để update badge số lượng giỏ hàng

**Chức năng:**
```java
@ModelAttribute
public void addCartCount(Model model) {
    // Lấy số lượng items trong giỏ từ database
    int count = cartService.getCartCount();
    model.addAttribute("cartCount", count);
}

@ModelAttribute  
public void addCurrentAccount(Model model) {
    // Inject thông tin user hiện tại
    Account account = getCurrentAccount();
    model.addAttribute("currentAccount", account);
}
```

---

## 2. Thay đổi Frontend

### 2.1. product-detail.html
**File:** `src/main/resources/templates/pages/product-detail.html`

**Thay đổi:**
- ✅ **Xóa toàn bộ script tags** (line 181-368)
- ✅ **Chuyển "Thêm vào giỏ" sang form submit:**
  ```html
  <form th:action="@{/product/add-to-cart}" method="post">
    <input type="hidden" name="productId" th:value="${product.id}">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
    
    <!-- Radio buttons cho màu sắc -->
    <input type="radio" name="color" value="Trắng" checked>
    
    <!-- Radio buttons cho kích thước -->
    <input type="radio" name="size" value="M" checked>
    
    <!-- Input số lượng -->
    <input type="number" name="qty" value="1" min="1">
    
    <!-- Submit button -->
    <button type="submit" class="btn btn-dark">Thêm vào giỏ</button>
  </form>
  ```

- ✅ **Xóa inline event handlers:**
  - Xóa `onclick="pdOpenLightbox()"`
  - Xóa `onmouseenter="pdPreview(this)"`
  - Giữ lại `onerror` handlers cho fallback images

**Backend handler đã có sẵn:**
```java
// ProductController.java - line 82
@PostMapping("/product/add-to-cart")
public String addToCart(
    @RequestParam("productId") Integer productId,
    @RequestParam("qty") Integer qty,
    @RequestParam("color") String color,
    @RequestParam("size") String size,
    RedirectAttributes ra
){
    cartService.addToCart(productId, qty, color, size);
    return "redirect:/pages/cart";
}
```

### 2.2. product-list.html
**File:** `src/main/resources/templates/pages/product-list.html`

**Thay đổi:**
- ✅ **Xóa toàn bộ script tags** (line 131-173)
- ✅ JavaScript đã được xóa bao gồm:
  - `updateBadge()` - không cần vì có GlobalControllerAdvice
  - `addItem()` - không cần vì add to cart ở trang detail
  - Event listeners cho `.add-to-cart` và `.to-detail`

**Lưu ý:** Trang này chỉ hiển thị danh sách sản phẩm, không có chức năng add to cart trực tiếp.

### 2.3. cart.html
**File:** `src/main/resources/templates/pages/cart.html`

**Thay đổi:**
- ✅ **Chuyển từ localStorage sang Backend API**
- ✅ Sử dụng AJAX để gọi các REST endpoints:
  - `GET /api/cart/items` - Lấy danh sách giỏ hàng
  - `PUT /api/cart/items/{cartId}` - Cập nhật số lượng
  - `DELETE /api/cart/items/{cartId}` - Xóa item

**Code mẫu:**
```javascript
// Load cart từ backend
async function loadCart(){
  const res = await fetch('/api/cart/items', {
    headers: { [csrfHeader]: csrfToken }
  });
  return await res.json();
}

// Update quantity
async function updateQty(cartId, newQty){
  const res = await fetch(`/api/cart/items/${cartId}`, {
    method: 'PUT',
    headers: { 
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken 
    },
    body: JSON.stringify({qty: newQty})
  });
  return res.ok;
}
```

---

## 3. Backend Controllers & Services đã có sẵn

### 3.1. CartController (REST API)
**File:** `src/main/java/poly/edu/controller/CartController.java`

**Endpoints:**
- `POST /api/cart/items` - Thêm sản phẩm vào giỏ
- `GET /api/cart/items` - Lấy tất cả items
- `GET /api/cart/count` - Lấy số lượng items
- `PUT /api/cart/items/{cartId}` - Cập nhật quantity
- `DELETE /api/cart/items/{cartId}` - Xóa item
- `DELETE /api/cart/items` - Xóa toàn bộ giỏ

### 3.2. CartService
**File:** `src/main/java/poly/edu/service/CartService.java`

**Các method:**
- `addToCart(productId, qty, color, size)` - Thêm vào giỏ
- `getCartItems()` - Lấy tất cả items của user hiện tại
- `getCartCount()` - Đếm tổng số lượng
- `updateQuantity(cartId, newQty)` - Cập nhật số lượng
- `removeFromCart(cartId)` - Xóa item
- `clearCart()` - Xóa toàn bộ

**Đặc điểm:**
- Tự động lấy user từ SecurityContext
- Lưu vào database (không dùng localStorage)
- Hỗ trợ color và size cho mỗi item

### 3.3. ProductController
**File:** `src/main/java/poly/edu/controller/ProductController.java`

**Methods liên quan:**
- `GET /products` - Danh sách sản phẩm với filter/search/sort
- `GET /product/detail/{id}` - Chi tiết sản phẩm
- `POST /product/add-to-cart` - Thêm vào giỏ (form submit)

---

## 4. Luồng hoạt động mới

### 4.1. Luồng xem sản phẩm và thêm vào giỏ

```
1. User truy cập /products
   └─> ProductController.productList()
       └─> Load products từ DB
       └─> GlobalControllerAdvice inject cartCount
       └─> Render product-list.html (không có script)

2. User click vào sản phẩm
   └─> Navigate to /product/detail/{id}
       └─> ProductController.productDetail()
           └─> Load product, images, reviews từ DB
           └─> Render product-detail.html

3. User chọn màu, size, số lượng và click "Thêm vào giỏ"
   └─> Form POST to /product/add-to-cart
       └─> ProductController.addToCart()
           └─> CartService.addToCart()
               └─> Save to database (Cart entity)
           └─> Redirect to /pages/cart

4. User xem giỏ hàng
   └─> Navigate to /pages/cart
       └─> cart.html AJAX call to /api/cart/items
           └─> CartController.getCartItems()
               └─> CartService.getCartItems()
                   └─> Load from database
           └─> Render cart items dynamically
```

### 4.2. Luồng update giỏ hàng

```
1. User click "+/-" để thay đổi số lượng
   └─> AJAX PUT /api/cart/items/{cartId}
       └─> CartController.updateQuantity()
           └─> CartService.updateQuantity()
               └─> Update database
       └─> Reload cart

2. User click "Xóa" 
   └─> AJAX DELETE /api/cart/items/{cartId}
       └─> CartController.removeFromCart()
           └─> CartService.removeFromCart()
               └─> Delete from database
       └─> Reload cart
```

### 4.3. Cart badge update (Navbar)

```
Mỗi lần render page:
└─> GlobalControllerAdvice.addCartCount()
    └─> CartService.getCartCount()
        └─> Query database, tính tổng quantity
    └─> model.addAttribute("cartCount", count)
        └─> Navbar hiển thị badge với số lượng
```

**Template (navbar.html):**
```html
<a class="nav-link" th:href="@{/pages/cart}">
  Giỏ hàng
  <span th:if="${cartCount > 0}" 
        class="badge bg-dark" 
        th:text="${cartCount}">0</span>
</a>
```

---

## 5. Lợi ích của Migration

### ✅ Bảo mật
- Không lưu dữ liệu nhạy cảm trong localStorage
- CSRF protection cho tất cả requests
- Session-based authentication

### ✅ Đồng bộ dữ liệu
- Giỏ hàng đồng bộ giữa các devices
- Không bị mất dữ liệu khi clear browser cache
- Dữ liệu lưu trong database, có thể backup

### ✅ Hiệu năng
- Giảm JavaScript, tăng tốc load page
- Server-side rendering tối ưu hơn
- Không cần parse/stringify JSON liên tục

### ✅ Maintainability
- Code tập trung ở backend, dễ maintain
- Validation logic ở một nơi (backend)
- Dễ debug và test

---

## 6. Cách rebuild project

### 6.1. Dọn dẹp build cũ
```bash
# Xóa thư mục bin (Eclipse)
rm -rf bin/

# Xóa thư mục target (Maven)
mvn clean
```

### 6.2. Build lại project
```bash
# Maven build
mvn clean install

# Hoặc build trong IDE (Eclipse/IntelliJ)
Right-click project → Maven → Update Project
```

### 6.3. Chạy ứng dụng
```bash
# Maven
mvn spring-boot:run

# Hoặc run Application.java trong IDE
```

---

## 7. Testing Checklist

### ✅ Product List
- [ ] Hiển thị danh sách sản phẩm
- [ ] Filter/Search/Sort hoạt động
- [ ] Pagination hoạt động
- [ ] Không có JavaScript errors

### ✅ Product Detail
- [ ] Hiển thị thông tin sản phẩm
- [ ] Chọn màu/size
- [ ] Nhập số lượng
- [ ] Click "Thêm vào giỏ" → redirect to cart
- [ ] Badge navbar tăng số lượng

### ✅ Cart
- [ ] Hiển thị items từ database
- [ ] Tăng/giảm số lượng hoạt động
- [ ] Xóa item hoạt động
- [ ] Tính tổng tiền chính xác
- [ ] "Thanh toán" button hoạt động

### ✅ Navbar
- [ ] Cart badge hiển thị đúng số lượng
- [ ] User avatar hiển thị (nếu đăng nhập)
- [ ] Dropdown menu hoạt động

---

## 8. Lưu ý quan trọng

### ⚠️ Authentication Required
Tất cả chức năng cart yêu cầu user phải đăng nhập. Nếu user chưa đăng nhập:
- CartService sẽ throw exception "User not authenticated"
- Cần redirect to login page

### ⚠️ CSRF Token
Tất cả AJAX requests cần include CSRF token:
```javascript
const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

fetch('/api/cart/items', {
  headers: { [csrfHeader]: csrfToken }
});
```

### ⚠️ Database Schema
Đảm bảo table `Cart` có đúng cấu trúc:
```sql
CREATE TABLE Cart (
  id INT PRIMARY KEY AUTO_INCREMENT,
  account_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL,
  color VARCHAR(50),
  size VARCHAR(50),
  FOREIGN KEY (account_id) REFERENCES Account(id),
  FOREIGN KEY (product_id) REFERENCES Product(id)
);
```

---

## 9. Files đã thay đổi

### Backend
1. ✅ `src/main/java/poly/edu/config/GlobalControllerAdvice.java` - **MỚI**
2. ✅ `src/main/java/poly/edu/controller/PageController.java` - Updated cart() method

### Frontend
3. ✅ `src/main/resources/templates/pages/product-detail.html` - Xóa scripts, thêm form
4. ✅ `src/main/resources/templates/pages/product-list.html` - Xóa scripts
5. ✅ `src/main/resources/templates/pages/cart.html` - Chuyển từ localStorage sang API

### Existing (không thay đổi)
- `src/main/java/poly/edu/controller/CartController.java` - Đã có sẵn
- `src/main/java/poly/edu/service/CartService.java` - Đã có sẵn
- `src/main/java/poly/edu/controller/ProductController.java` - Đã có sẵn

---

## 10. Next Steps (Tùy chọn)

### Cải tiến thêm:
1. **Tạo file JS riêng** cho cart.html thay vì inline script
2. **Thêm loading spinner** khi AJAX đang fetch
3. **Toast notifications** khi add to cart thành công
4. **Real-time update** cart badge không cần reload page
5. **Optimize**: Thêm caching cho cart count

---

**Tác giả:** Cascade AI  
**Ngày:** 2025-11-07  
**Dự án:** SOF3022 - Shop OMG!
