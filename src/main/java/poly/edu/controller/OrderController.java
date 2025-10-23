package poly.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.OrderRequestDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Order; // Sửa từ CustomerOrder thành Order
import poly.edu.repository.OrderRepository;
import poly.edu.service.OrderService;
import poly.edu.entity.OrderDetail; // Thêm import này
import poly.edu.repository.OrderDetailRepository; // Thêm import này

import java.util.List;

// Controller xử lý các yêu cầu liên quan đến đơn hàng của user
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService; // Service xử lý logic đơn hàng
    private final OrderRepository orderRepository; // Repository truy vấn đơn hàng
    private final OrderDetailRepository orderDetailRepository; // Repository truy vấn chi tiết đơn hàng

    /**
     * API để nhận đơn hàng từ frontend
     * @param orderRequest: Thông tin đơn hàng (sản phẩm, thông tin giao hàng)
     * @param currentUser: User đang đăng nhập
     */
    @PostMapping("/orders/place")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequest,
                                        @AuthenticationPrincipal Account currentUser) {
        // Kiểm tra user đã đăng nhập chưa
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để đặt hàng");
        }
        try {
            // Gọi service để xử lý logic đặt hàng
            orderService.placeOrder(orderRequest, currentUser); 
            return ResponseEntity.ok().body("Đặt hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi đặt hàng: " + e.getMessage());
        }
    }

    /**
     * Hiển thị trang danh sách đơn hàng của user
     */
    @GetMapping("/pages/order-list")
    public String showOrderList(Model model, @AuthenticationPrincipal Account currentUser) {
        // Kiểm tra user đã đăng nhập chưa
        if (currentUser == null) {
            return "redirect:/auth/login"; // Yêu cầu đăng nhập
        }
        
        // Lấy danh sách đơn hàng của user, sắp xếp theo ngày giảm dần
        List<Order> orders = orderRepository.findByAccountOrderByOrderDateDesc(currentUser);
        
        model.addAttribute("orders", orders);
        return "pages/order-list"; // Trả về file order-list.html
    }
    /**
     * Hiển thị trang chi tiết đơn hàng
     */
    @GetMapping("/orders/{id}") 
    public String showOrderDetail(@PathVariable("id") Integer orderId, Model model, @AuthenticationPrincipal Account currentUser) {
        // Kiểm tra user đã đăng nhập chưa
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Tìm đơn hàng, đảm bảo nó thuộc về người dùng đang đăng nhập
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getAccount().getId().equals(currentUser.getId())) {
            return "redirect:/pages/order-list"; // Nếu không tìm thấy hoặc không đúng chủ, quay về trang danh sách
        }

        // Lấy danh sách chi tiết sản phẩm của đơn hàng
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("details", details);
        return "pages/order-detail"; // Trả về file order-detail.html
    }
}