package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.entity.Order;
import poly.edu.entity.OrderDetail;
import poly.edu.service.OrderDetailService;
import poly.edu.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    // Danh sách đơn hàng của người dùng
    @GetMapping
    public String listOrders(Model model, @RequestParam(required = false) String status) {
        var orders = (status == null)
                ? orderService.getAllOrders()
                : orderService.getOrdersByStatus(status);
        model.addAttribute("orders", orders);
        return "pages/order-list"; // file HTML bạn đã có
    }

    // Chi tiết đơn hàng
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {
        var order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/orders"; // nếu không tồn tại thì quay về danh sách
        }
        List<OrderDetail> details = orderDetailService.getDetailsByOrderId(id);

        model.addAttribute("order", order);
        model.addAttribute("details", details);
        return "pages/order-detail"; // file HTML chi tiết đơn hàng
    }
}
