package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.entity.Account;
import poly.edu.entity.Order;
import java.time.LocalDateTime;
import java.util.List;

// Repository quản lý truy vấn dữ liệu cho đơn hàng
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Tìm đơn hàng theo ID tài khoản
    List<Order> findByAccountId(Integer accountId);
    
    // Tìm đơn hàng theo trạng thái
    List<Order> findByStatus(String status);
    
    // Tìm đơn hàng theo tài khoản, sắp xếp theo ngày giảm dần
    List<Order> findByAccountOrderByOrderDateDesc(Account account);
    
    // Tìm đơn hàng hoàn thành trong khoảng thời gian
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status = 'COMPLETED'")
    List<Order> findCompletedOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Tìm tất cả đơn hàng hoàn thành
    @Query("SELECT o FROM Order o WHERE o.status = 'COMPLETED'")
    List<Order> findCompletedOrders();
}
