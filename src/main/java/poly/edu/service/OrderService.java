package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dto.CartItemDTO;
import poly.edu.dto.OrderDTO;
import poly.edu.entity.Order;
import poly.edu.repository.OrderRepository;
import poly.edu.dto.OrderRequestDTO;
import poly.edu.dto.ShippingInfoDTO;
import poly.edu.entity.Account;
import poly.edu.entity.OrderDetail;
import poly.edu.entity.Product;
import poly.edu.repository.OrderDetailRepository; // Thêm
import poly.edu.repository.ProductRepository;   // Thêm

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository; // Thêm
    private final OrderDetailRepository orderDetailRepository; // Thêm

    // Phí ship cố định (ví dụ)
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("30000");

    /**
     * Hàm xử lý logic đặt hàng
     */
    @Transactional
    public Order placeOrder(OrderRequestDTO orderRequest, Account currentUser) {
        // 1. Tạo đối tượng Order mới
        Order order = new Order();
        ShippingInfoDTO shippingInfo = orderRequest.getShippingInfo();

        // 2. Map thông tin người nhận từ DTO vào Order
        order.setAccount(currentUser); // Người dùng đang đăng nhập
        order.setReceiverName(shippingInfo.getFullName());
        order.setReceiverPhone(shippingInfo.getPhone());
        order.setReceiverAddress(shippingInfo.getAddress());
        // (email người nhận có thể thêm vào entity Order nếu bạn muốn)

        // 3. Xử lý danh sách sản phẩm
        List<OrderDetail> details = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO; // Tổng tiền hàng

        for (CartItemDTO itemDTO : orderRequest.getCartItems()) {
            // Lấy sản phẩm từ DB để đảm bảo giá đúng
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + itemDTO.getProductId()));

            // Tạo OrderDetail
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(itemDTO.getQty());
            
            // Lấy giá từ DB (không tin giá client)
            detail.setPrice(product.getPrice()); 

            // Lưu lại thông tin (từ entity Product và DTO)
            detail.setProductName(product.getName());
            detail.setColor(itemDTO.getColor());
            detail.setSize(itemDTO.getSize());

            details.add(detail);

            // Cộng dồn vào tổng tiền hàng
            subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQty())));
        }

        // 4. Gán danh sách chi tiết và tổng tiền
        order.setOrderDetails(details);
        order.setTotalAmount(subtotal.add(SHIPPING_FEE)); // Tổng tiền = Tiền hàng + Phí ship
        order.setStatus("PENDING"); // Trạng thái chờ xử lý

        // 5. Lưu Order (và OrderDetail sẽ tự lưu nhờ Cascade)
        return orderRepository.save(order);
    }


    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return getAllOrders();
        }
        return orderRepository.findByStatus(status.toUpperCase()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Integer id) {
        return orderRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Integer id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return null;
        
        order.setStatus(status.toUpperCase());
        return convertToDTO(orderRepository.save(order));
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        if (order.getAccount() != null) {
            dto.setAccountId(order.getAccount().getId());
            dto.setCustomerName(order.getAccount().getFullName());
        }
        return dto;
    }
}
