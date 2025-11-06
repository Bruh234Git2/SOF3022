package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.entity.Cart;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByAccountId(Integer accountId);
    Optional<Cart> findByAccountIdAndProductId(Integer accountId, Integer productId);
    Optional<Cart> findByAccountIdAndProductIdAndColorAndSize(Integer accountId, Integer productId, String color, String size);
}
