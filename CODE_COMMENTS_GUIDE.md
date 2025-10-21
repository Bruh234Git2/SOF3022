# Hướng Dẫn Code - Admin ShopOMG

## Cấu Trúc Project

### 1. Entities (Entity Classes)
Các class đại diện cho bảng trong database:

- **Category** - Danh mục sản phẩm
- **Product** - Sản phẩm (có quan hệ ManyToOne với Category)
- **Order** - Đơn hàng (có quan hệ ManyToOne với Account)
- **OrderDetail** - Chi tiết đơn hàng
- **ProductImage** - Ảnh phụ của sản phẩm
- **ProductReview** - Đánh giá sản phẩm
- **Cart** - Giỏ hàng
- **Report** - Báo cáo doanh thu

**Annotation quan trọng:**
- `@Entity` - Đánh dấu class là entity
- `@Table(name="...")` - Mapping với tên bảng trong DB
- `@Id` - Khóa chính
- `@GeneratedValue` - Tự động tăng
- `@ManyToOne` - Quan hệ nhiều-một
- `@OneToMany` - Quan hệ một-nhiều
- `@PrePersist` - Chạy trước khi lưu vào DB

### 2. Repositories
Interface kế thừa JpaRepository để thao tác với database:

```java
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // JpaRepository cung cấp sẵn: findAll(), findById(), save(), deleteById()
}
```

**Custom Query Methods:**
- `findByNameContainingIgnoreCase()` - Tìm kiếm không phân biệt hoa thường
- `findByStatus()` - Lọc theo trạng thái
- `@Query` - Viết JPQL tùy chỉnh

### 3. DTOs (Data Transfer Objects)
Class chuyển dữ liệu giữa các layer, không mapping trực tiếp với DB:

- **CategoryDTO** - Dữ liệu danh mục trả về API
- **ProductDTO** - Dữ liệu sản phẩm (bao gồm tên danh mục)
- **OrderDTO** - Dữ liệu đơn hàng (bao gồm tên khách hàng)
- **AccountDTO** - Dữ liệu tài khoản
- **RevenueDTO** - Dữ liệu báo cáo doanh thu
- **VIPCustomerDTO** - Dữ liệu khách hàng VIP

### 4. Services
Xử lý logic nghiệp vụ:

**CategoryService:**
- `getAllCategories()` - Lấy tất cả danh mục
- `createCategory()` - Tạo danh mục mới
- `updateCategory()` - Cập nhật danh mục
- `deleteCategory()` - Xóa danh mục
- `convertToDTO()` - Chuyển Entity sang DTO

**ProductService:**
- `searchProducts(keyword)` - Tìm kiếm sản phẩm
- CRUD operations tương tự CategoryService

**OrderService:**
- `getOrdersByStatus()` - Lọc đơn hàng theo trạng thái
- `updateOrderStatus()` - Cập nhật trạng thái đơn hàng

**RevenueService:**
- `calculateRevenue()` - Tính tổng doanh thu theo khoảng thời gian
- `getVIPCustomers()` - Lấy danh sách khách VIP theo ngưỡng chi tiêu

**Annotation quan trọng:**
- `@Service` - Đánh dấu class là service
- `@Transactional` - Đảm bảo transaction khi thao tác DB
- `@RequiredArgsConstructor` - Tự động inject dependencies

### 5. Controller
Xử lý HTTP requests:

**AdminController:**
- Tất cả endpoint bắt đầu với `/admin`
- Yêu cầu role ADMIN (`@PreAuthorize("hasRole('ADMIN')")`)

**API Endpoints (trả về JSON):**
- `GET /admin/api/categories` - Lấy danh sách
- `POST /admin/api/categories` - Tạo mới
- `PUT /admin/api/categories/{id}` - Cập nhật
- `DELETE /admin/api/categories/{id}` - Xóa

**Page Endpoints (trả về HTML):**
- `GET /admin/category` - Trang quản lý danh mục
- `GET /admin/product` - Trang quản lý sản phẩm
- `GET /admin/order` - Trang quản lý đơn hàng

**Annotation quan trọng:**
- `@Controller` - Đánh dấu class là controller
- `@RequestMapping("/admin")` - Base path cho tất cả endpoints
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` - HTTP methods
- `@ResponseBody` - Trả về JSON thay vì view
- `@PathVariable` - Lấy giá trị từ URL
- `@RequestParam` - Lấy query parameters
- `@RequestBody` - Lấy dữ liệu từ request body (JSON)

### 6. Frontend (HTML + JavaScript)

**Cấu trúc chung:**
1. HTML form/table để hiển thị và nhập liệu
2. JavaScript gọi API để CRUD
3. Bootstrap Modal cho form thêm/sửa

**Flow hoạt động:**

**Tải dữ liệu:**
```javascript
async function loadData() {
    const res = await fetch('/admin/api/categories'); // Gọi API
    const list = await res.json(); // Parse JSON
    renderData(list); // Hiển thị lên bảng
}
```

**Tạo mới:**
```javascript
const data = { name: '...', description: '...' };
await fetch('/admin/api/categories', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(data)
});
```

**Cập nhật:**
```javascript
await fetch(`/admin/api/categories/${id}`, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(data)
});
```

**Xóa:**
```javascript
await fetch(`/admin/api/categories/${id}`, {
    method: 'DELETE'
});
```

## Luồng Xử Lý Request

### Ví dụ: Tạo danh mục mới

1. **Frontend (category.html):**
   - User nhập tên, mô tả vào form
   - Click nút "Lưu"
   - JavaScript thu thập dữ liệu và gọi API POST

2. **Controller (AdminController):**
   ```java
   @PostMapping("/api/categories")
   public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
       return ResponseEntity.ok(categoryService.createCategory(dto));
   }
   ```
   - Nhận JSON từ request body
   - Gọi service để xử lý

3. **Service (CategoryService):**
   ```java
   @Transactional
   public CategoryDTO createCategory(CategoryDTO dto) {
       Category category = new Category();
       category.setName(dto.getName());
       category.setDescription(dto.getDescription());
       return convertToDTO(categoryRepository.save(category));
   }
   ```
   - Tạo entity mới
   - Lưu vào DB qua repository
   - Chuyển entity sang DTO để trả về

4. **Repository (CategoryRepository):**
   - JpaRepository tự động generate SQL INSERT
   - Lưu vào bảng Categories

5. **Response:**
   - Service trả DTO về Controller
   - Controller trả JSON về Frontend
   - Frontend hiển thị thông báo và reload danh sách

## Security

**SecurityConfig:**
- `@EnableMethodSecurity` - Bật method-level security
- Admin routes yêu cầu role ADMIN
- CSRF protection enabled

**Authentication:**
- Spring Security tự động kiểm tra role
- Nếu không có quyền -> 403 Forbidden

## Database Transaction

**@Transactional:**
- Đảm bảo tất cả operations trong method thành công
- Nếu có lỗi -> rollback tất cả
- Quan trọng cho operations CREATE, UPDATE, DELETE

## Best Practices

1. **Separation of Concerns:**
   - Entity: Đại diện dữ liệu
   - Repository: Truy vấn DB
   - Service: Logic nghiệp vụ
   - Controller: Xử lý HTTP
   - DTO: Chuyển dữ liệu

2. **RESTful API:**
   - GET: Lấy dữ liệu
   - POST: Tạo mới
   - PUT: Cập nhật
   - DELETE: Xóa

3. **Error Handling:**
   - Kiểm tra null trước khi xử lý
   - Trả về 404 nếu không tìm thấy
   - Try-catch trong JavaScript

4. **Frontend:**
   - Async/await cho API calls
   - Confirm trước khi xóa
   - Reload data sau mỗi thao tác
   - Reset form sau khi đóng modal

## Các Chức Năng Đã Implement

✅ **Quản lý Danh mục** - CRUD hoàn chỉnh
✅ **Quản lý Sản phẩm** - CRUD + tìm kiếm + chọn danh mục
✅ **Quản lý Đơn hàng** - Xem + cập nhật trạng thái + lọc
✅ **Quản lý Tài khoản** - Xem + sửa + xóa + lọc theo role
✅ **Báo cáo Doanh thu** - Tính theo khoảng thời gian
✅ **Khách hàng VIP** - Lọc theo ngưỡng chi tiêu

## Testing

**Cách test từng chức năng:**

1. Đăng nhập với tài khoản ADMIN
2. Truy cập `/admin/category` (hoặc các trang khác)
3. Test CRUD:
   - Tạo mới: Nhập dữ liệu -> Lưu -> Kiểm tra DB
   - Sửa: Click Sửa -> Thay đổi -> Lưu -> Kiểm tra DB
   - Xóa: Click Xóa -> Confirm -> Kiểm tra DB
4. Test tìm kiếm/lọc
5. Kiểm tra validation (trường bắt buộc, format...)

## Troubleshooting

**Lỗi thường gặp:**

1. **403 Forbidden:**
   - Kiểm tra role của user
   - Kiểm tra @PreAuthorize annotation

2. **404 Not Found:**
   - Kiểm tra URL mapping
   - Kiểm tra RequestMapping path

3. **500 Internal Server Error:**
   - Xem log trong console
   - Kiểm tra null pointer
   - Kiểm tra database connection

4. **CSRF Token Error:**
   - Đảm bảo include CSRF token trong form
   - Hoặc disable CSRF cho API endpoints

5. **JSON Parse Error:**
   - Kiểm tra Content-Type header
   - Kiểm tra format JSON trong request body
