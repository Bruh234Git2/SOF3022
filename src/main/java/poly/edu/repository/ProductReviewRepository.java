package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;
import poly.edu.entity.ProductReview;
import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    List<ProductReview> findByProductId(Integer productId);
=======
import poly.edu.entity.ProductReview;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    List<ProductReview> findByProduct_IdOrderByCreatedAtDesc(Integer productId);
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
}
