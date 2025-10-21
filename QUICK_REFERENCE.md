# Quick Reference Guide - Java5Ass

## 🚀 Khởi động nhanh

### Chạy ứng dụng
```bash
.\mvnw.cmd spring-boot:run
```

### Build project
```bash
.\mvnw.cmd clean package -DskipTests
```

### Compile only
```bash
.\mvnw.cmd clean compile
```

---

## 📁 Cấu trúc thư mục quan trọng

```
src/main/java/poly/edu/
├── controller/          # Xử lý HTTP requests
│   ├── AdminController.java      # API admin (CRUD)
│   ├── ProductController.java    # Hiển thị sản phẩm
│   ├── PageController.java       # Các trang tĩnh
│   ├── AccountController.java    # Đăng ký, profile
│   └── AuthController.java       # Login page
│
├── service/            # Business logic
│   ├── ProductService.java       # Logic sản phẩm
│   ├── OrderService.java         # Logic đơn hàng
│   ├── CategoryService.java      # Logic danh mục
│   ├── AccountService.java       # Logic tài khoản
│   ├── AdminAccountService.java  # Admin quản lý user
│   ├── RevenueService.java       # Báo cáo doanh thu
│   ├── OrderDetailService.java   # Chi tiết đơn hàng
│   └── FileService.java          # Upload file
│
├── repository/         # Database access
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   ├── CategoryRepository.java
│   └── ...
│
├── entity/            # JPA Entities
│   ├── Product.java
│   ├── Order.java
│   ├── Account.java
│   └── ...
│
├── dto/               # Data Transfer Objects
│   ├── ProductDTO.java
│   ├── OrderDTO.java
│   └── ...
│
├── security/          # Spring Security
│   ├── SecurityConfig.java
│   └── CustomUserDetailsService.java
│
└── config/           # Cấu hình khác
    ├── WebMvcConfig.java
    └── DataInitializer.java
```

---

## 🔑 Các endpoint quan trọng

### Public (không cần login)
| Method | URL | Mô tả |
|--------|-----|-------|
| GET | `/pages/home` | Trang chủ |
| GET | `/products` | Danh sách sản phẩm |
| GET | `/product/detail/{id}` | Chi tiết sản phẩm |
| GET | `/auth/login` | Trang đăng nhập |
| GET | `/account/sign-up` | Trang đăng ký |
| POST | `/account/sign-up` | Xử lý đăng ký |

### User (cần login)
| Method | URL | Mô tả |
|--------|-----|-------|
| GET | `/pages/cart` | Giỏ hàng |
| GET | `/pages/check-out` | Thanh toán |
| GET | `/pages/order-list` | Đơn hàng của tôi |
| GET | `/account/edit-profile` | Sửa profile |
| POST | `/account/edit-profile` | Cập nhật profile |

### Admin (cần role ADMIN)
| Method | URL | Mô tả |
|--------|-----|-------|
| GET | `/admin/category` | Trang quản lý danh mục |
| GET | `/admin/product` | Trang quản lý sản phẩm |
| GET | `/admin/order` | Trang quản lý đơn hàng |
| GET | `/admin/account` | Trang quản lý tài khoản |
| GET | `/admin/revenue` | Trang báo cáo doanh thu |
| GET | `/admin/vip` | Trang khách hàng VIP |

### Admin API (JSON)
| Method | URL | Mô tả |
|--------|-----|-------|
| GET | `/admin/api/categories` | Lấy danh sách danh mục |
| POST | `/admin/api/categories` | Tạo danh mục mới |
| PUT | `/admin/api/categories/{id}` | Cập nhật danh mục |
| DELETE | `/admin/api/categories/{id}` | Xóa danh mục |
| GET | `/admin/api/products` | Lấy danh sách sản phẩm |
| POST | `/admin/api/products` | Tạo sản phẩm mới |
| PUT | `/admin/api/products/{id}` | Cập nhật sản phẩm |
| DELETE | `/admin/api/products/{id}` | Xóa sản phẩm |
| GET | `/admin/api/orders` | Lấy danh sách đơn hàng |
| PUT | `/admin/api/orders/{id}/status` | Cập nhật trạng thái đơn |
| GET | `/admin/api/accounts` | Lấy danh sách tài khoản |
| GET | `/admin/api/revenue` | Báo cáo doanh thu |
| GET | `/admin/api/vip-customers` | Danh sách khách VIP |

---

## 🗄️ Database Schema

### Bảng chính

#### Accounts
```sql
- id (PK)
- username (unique)
- password (encrypted)
- full_name
- email (unique)
- phone
- address
- role_id (FK -> Roles)
- status
- created_at
- updated_at
- avatar
- birth_date
```

#### Products
```sql
- id (PK)
- name
- price
- discount
- image
- description
- category_id (FK -> Categories)
- gender
```

#### Orders
```sql
- id (PK)
- account_id (FK -> Accounts)
- order_date
- status
- total_amount
- receiver_name
- receiver_phone
- receiver_address
```

#### OrderDetails
```sql
- id (PK)
- order_id (FK -> Orders)
- product_id (FK -> Products)
- quantity
- price
```

#### Categories
```sql
- id (PK)
- name
- description
```

#### Roles
```sql
- id (PK)
- name (USER, ADMIN)
```

---

## 🔐 Security Configuration

### Quyền truy cập

**Public (không cần login):**
- `/`, `/auth/**`, `/account/sign-up`
- `/pages/**` (hầu hết các trang)
- `/css/**`, `/images/**`, `/js/**`, `/uploads/**`

**Admin only:**
- `/pages/admin/**`
- `/admin/**`

**Authenticated (đã login):**
- Tất cả các endpoint khác

### Login Configuration
- **Login URL:** `/auth/login`
- **Username field:** `email`
- **Password field:** `password`
- **Success URL:** `/pages/home`
- **Failure URL:** `/auth/login?error`
- **Remember Me:** 7 ngày

---

## 📝 Các file cấu hình

### application.properties
```properties
# Database
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ShopOMG
spring.datasource.username=sa1
spring.datasource.password=123

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# File Upload
spring.servlet.multipart.max-file-size=5MB
file.upload-dir=d:/SOF3022_laptrinhjava5/BaiTapJava5/Java5Ass/uploads

# Thymeleaf
spring.thymeleaf.cache=false

# Mail (nếu cần)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
```

### pom.xml - Dependencies chính
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- Thymeleaf Extras Spring Security
- SQL Server JDBC Driver
- Lombok
- Bean Validation

---

## 🛠️ Các lệnh Maven hữu ích

```bash
# Clean project
.\mvnw.cmd clean

# Compile
.\mvnw.cmd compile

# Run tests
.\mvnw.cmd test

# Package (tạo JAR)
.\mvnw.cmd package

# Skip tests khi package
.\mvnw.cmd package -DskipTests

# Run application
.\mvnw.cmd spring-boot:run

# Clean và compile
.\mvnw.cmd clean compile

# Clean và package
.\mvnw.cmd clean package -DskipTests
```

---

## 🐛 Troubleshooting

### Lỗi compilation
```bash
# Clean và rebuild
.\mvnw.cmd clean compile
```

### Lỗi database connection
1. Kiểm tra SQL Server đang chạy
2. Kiểm tra username/password trong `application.properties`
3. Kiểm tra database `ShopOMG` đã được tạo chưa

### Lỗi port đã được sử dụng
```properties
# Thêm vào application.properties để đổi port
server.port=8081
```

### Lỗi upload file
1. Kiểm tra thư mục `uploads` đã tồn tại chưa
2. Kiểm tra quyền ghi file
3. Kiểm tra kích thước file (max 5MB)

### Lỗi 403 Forbidden
1. Kiểm tra user đã login chưa
2. Kiểm tra user có role phù hợp không (ADMIN cho admin pages)
3. Kiểm tra CSRF token (đã được cấu hình tự động)

---

## 📚 Code Examples

### Tạo sản phẩm mới (Admin API)
```javascript
fetch('/admin/api/products', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        name: 'Sản phẩm mới',
        price: 100000,
        discount: 10,
        image: '/uploads/image.jpg',
        description: 'Mô tả sản phẩm',
        categoryId: 1
    })
})
.then(response => response.json())
.then(data => console.log(data));
```

### Cập nhật trạng thái đơn hàng
```javascript
fetch('/admin/api/orders/1/status', {
    method: 'PUT',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify('COMPLETED')
})
.then(response => response.json())
.then(data => console.log(data));
```

### Lấy danh sách sản phẩm theo danh mục
```java
// ProductService.java
public Page<Product> search(Map<String, String> params) {
    // Tự động filter theo category, price, keyword, etc.
    // Xem ProductService.java để biết chi tiết
}
```

---

## 🎯 Best Practices

### Service Layer
- Luôn sử dụng `@Transactional` cho các method thay đổi dữ liệu
- Validate dữ liệu trước khi lưu vào database
- Trả về DTO thay vì Entity

### Controller Layer
- Sử dụng `@Valid` để validate input
- Xử lý lỗi và trả về message phù hợp
- Sử dụng `ResponseEntity` cho REST API

### Repository Layer
- Đặt tên method theo Spring Data JPA convention
- Sử dụng `@Query` cho query phức tạp
- Tối ưu query với `JOIN FETCH` khi cần

### Security
- Không hardcode password
- Luôn encode password trước khi lưu
- Kiểm tra quyền truy cập ở cả Controller và Service

---

## 📞 Support

Nếu gặp vấn đề:
1. Kiểm tra log trong console
2. Kiểm tra file `PROJECT_FIX_SUMMARY.md`
3. Kiểm tra database connection
4. Đảm bảo Java 17 được cài đặt

---

**Last Updated:** 21/10/2025
