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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    /**
     * API để nhận đơn hàng
     */
    @PostMapping("/orders/place")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequest,
                                        @AuthenticationPrincipal Account currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để đặt hàng");
        }
        try {
            // Gọi hàm placeOrder đã tạo
            orderService.placeOrder(orderRequest, currentUser); 
            return ResponseEntity.ok().body("Đặt hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi đặt hàng: " + e.getMessage());
        }
    }

    /**
     * Hiển thị trang danh sách đơn hàng (thay cho PageController)
     */
    @GetMapping("/pages/order-list")
    public String showOrderList(Model model, @AuthenticationPrincipal Account currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login"; // Yêu cầu đăng nhập
        }
        
        // SỬA LẠI DÒNG NÀY
        // Truyền thẳng đối tượng 'currentUser' (Account)
        List<Order> orders = orderRepository.findByAccountOrderByOrderDateDesc(currentUser);
        
        model.addAttribute("orders", orders);
        return "pages/order-list"; // Trả về file order-list.html
    }
}