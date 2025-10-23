package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.dto.PurchasedItem;
import poly.edu.entity.OrderDetail;

import java.util.List;

// Repository quản lý truy vấn dữ liệu cho chi tiết đơn hàng
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Tìm chi tiết đơn hàng theo order ID
    List<OrderDetail> findByOrderId(Integer orderId);

    // Tìm sản phẩm đã mua của khách hàng theo email và trạng thái đơn hàng
    // Group by sản phẩm và tính tổng số lượng đã mua
    @Query("select new poly.edu.dto.PurchasedItem(d.product, sum(d.quantity)) " +
           "from OrderDetail d " +
           "where d.order.account.email = :email and upper(d.order.status) in :statuses " +
           "group by d.product")
    List<PurchasedItem> findPurchasedItems(@Param("email") String email,
                                           @Param("statuses") List<String> upperStatuses);
}
