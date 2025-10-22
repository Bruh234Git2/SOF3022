package poly.edu.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dto.OrderDTO;
import poly.edu.entity.Order;
import poly.edu.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository; // Sử dụng repo để tạo dữ liệu test

    // TC015: Lấy tất cả đơn hàng khi không có đơn nào
    @Test
    void testGetAllOrders_Empty() {
        orderRepository.deleteAll();
        List<OrderDTO> orders = orderService.getAllOrders();
        assertTrue(orders.isEmpty());
    }

    // TC016: Lấy đơn hàng theo ID tồn tại
    @Test
    void testGetOrderById_Found() {
        Order order = new Order();
        order.setStatus("PENDING");
        order.setTotalAmount(new BigDecimal("500000"));
        Order saved = orderRepository.save(order);
        OrderDTO found = orderService.getOrderById(saved.getId());
        assertNotNull(found);
    }
    
    // TC017: Lấy đơn hàng theo ID không tồn tại
    @Test
    void testGetOrderById_NotFound() {
        OrderDTO found = orderService.getOrderById(8888);
        assertNull(found);
    }

    // TC018: Cập nhật trạng thái đơn hàng thành công
    @Test
    void testUpdateOrderStatus_Success() {
        Order order = new Order();
        order.setStatus("PENDING");
        Order saved = orderRepository.save(order);

        OrderDTO updated = orderService.updateOrderStatus(saved.getId(), "SHIPPING");
        assertEquals("SHIPPING", updated.getStatus());
    }

    // TC019: Lọc đơn hàng theo trạng thái 'COMPLETED'
    @Test
    void testGetOrdersByStatus_Completed() {
        Order o1 = new Order(); o1.setStatus("PENDING"); orderRepository.save(o1);
        Order o2 = new Order(); o2.setStatus("COMPLETED"); orderRepository.save(o2);

        List<OrderDTO> completedOrders = orderService.getOrdersByStatus("COMPLETED");
        assertEquals(1, completedOrders.size());
        assertEquals("COMPLETED", completedOrders.get(0).getStatus());
    }

    // TC020: Cập nhật trạng thái cho đơn hàng không tồn tại
    @Test
    void testUpdateOrderStatus_NotFound() {
        OrderDTO updated = orderService.updateOrderStatus(8888, "COMPLETED");
        assertNull(updated);
    }
}