package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;
import poly.edu.entity.OrderDetail;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderId(Integer orderId);
=======
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.dto.PurchasedItem;
import poly.edu.entity.OrderDetail;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("select new poly.edu.dto.PurchasedItem(d.product, sum(d.quantity)) " +
           "from OrderDetail d " +
           "where d.order.account.email = :email and upper(d.order.status) in :statuses " +
           "group by d.product")
    List<PurchasedItem> findPurchasedItems(@Param("email") String email,
                                           @Param("statuses") List<String> upperStatuses);
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
}
