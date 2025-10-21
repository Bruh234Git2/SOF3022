package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.*;
import poly.edu.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller quản lý tất cả chức năng admin
 * Chỉ user có role ADMIN mới truy cập được
 */
@Controller
@RequestMapping("/admin") // Tất cả endpoint bắt đầu bằng /admin
@PreAuthorize("hasRole('ADMIN')") // Yêu cầu quyền ADMIN
@RequiredArgsConstructor // Tự động inject các service
public class AdminController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;
    private final AdminAccountService adminAccountService;
    private final RevenueService revenueService;

    // ==================== CATEGORY ENDPOINTS ====================
    // Quản lý danh mục sản phẩm
    
    @GetMapping("/api/categories") // GET: Lấy danh sách tất cả danh mục
    @ResponseBody // Trả về JSON
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/api/categories/{id}")
    @ResponseBody
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Integer id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return category != null ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
    }

    @PostMapping("/api/categories") // POST: Tạo danh mục mới
    @ResponseBody
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    @PutMapping("/api/categories/{id}") // PUT: Cập nhật danh mục
    @ResponseBody
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Integer id, @RequestBody CategoryDTO dto) {
        CategoryDTO updated = categoryService.updateCategory(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/categories/{id}") // DELETE: Xóa danh mục
    @ResponseBody
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        return categoryService.deleteCategory(id) ? 
                ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ==================== PRODUCT ENDPOINTS ====================
    // Quản lý sản phẩm
    
    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<ProductDTO>> getAllProducts(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    @GetMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        ProductDTO product = productService.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PostMapping("/api/products")
    @ResponseBody
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @PutMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO dto) {
        ProductDTO updated = productService.updateProduct(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id) ? 
                ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ==================== ORDER ENDPOINTS ====================
    // Quản lý đơn hàng
    
    @GetMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<List<OrderDTO>> getAllOrders(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/api/orders/{id}")
    @ResponseBody
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        OrderDTO order = orderService.getOrderById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PutMapping("/api/orders/{id}/status")
    @ResponseBody
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Integer id, @RequestBody String status) {
        OrderDTO updated = orderService.updateOrderStatus(id, status);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // ==================== ACCOUNT ENDPOINTS ====================
    // Quản lý tài khoản
    
    @GetMapping("/api/accounts")
    @ResponseBody
    public ResponseEntity<List<AccountDTO>> getAllAccounts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(adminAccountService.searchAccounts(keyword, role));
    }

    @GetMapping("/api/accounts/{id}")
    @ResponseBody
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Integer id) {
        AccountDTO account = adminAccountService.getAccountById(id);
        return account != null ? ResponseEntity.ok(account) : ResponseEntity.notFound().build();
    }

    @PutMapping("/api/accounts/{id}")
    @ResponseBody
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Integer id, @RequestBody AccountDTO dto) {
        AccountDTO updated = adminAccountService.updateAccount(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/accounts/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        return adminAccountService.deleteAccount(id) ? 
                ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ==================== REVENUE ENDPOINTS ====================
    // Báo cáo doanh thu
    
    @GetMapping("/api/revenue")
    @ResponseBody
    public ResponseEntity<RevenueDTO> calculateRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(revenueService.calculateRevenue(startDate, endDate));
    }

    @GetMapping("/api/revenue/orders")
    @ResponseBody
    public ResponseEntity<List<OrderDTO>> getCompletedOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(revenueService.getCompletedOrders(startDate, endDate));
    }

    // ==================== VIP CUSTOMER ENDPOINTS ====================
    // Quản lý khách hàng VIP
    
    @GetMapping("/api/vip-customers")
    @ResponseBody
    public ResponseEntity<List<VIPCustomerDTO>> getVIPCustomers(
            @RequestParam(required = false, defaultValue = "1000000") BigDecimal threshold) {
        return ResponseEntity.ok(revenueService.getVIPCustomers(threshold));
    }

    // ==================== PAGE VIEWS ====================
    // Trả về các trang HTML admin
    
    @GetMapping("/category")
    public String categoryPage() {
        return "pages/admin/category";
    }

    @GetMapping("/product")
    public String productPage() {
        return "pages/admin/product";
    }

    @GetMapping("/order")
    public String orderPage() {
        return "pages/admin/order";
    }

    @GetMapping("/account")
    public String accountPage() {
        return "pages/admin/account";
    }

    @GetMapping("/revenue")
    public String revenuePage() {
        return "pages/admin/revenue";
    }

    @GetMapping("/vip")
    public String vipPage() {
        return "pages/admin/vip";
    }
}
