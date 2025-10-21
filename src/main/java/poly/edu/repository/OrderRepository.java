package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.edu.entity.Order;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByAccountId(Integer accountId);
    List<Order> findByStatus(String status);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status = 'COMPLETED'")
    List<Order> findCompletedOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = 'COMPLETED'")
    List<Order> findCompletedOrders();
}
