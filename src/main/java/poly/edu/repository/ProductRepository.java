package poly.edu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import poly.edu.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    @Query("select p from Product p left join ProductReview r on r.product = p group by p order by coalesce(avg(r.rating),0) desc, count(r.id) desc, p.id desc")
    List<Product> findTopRated(Pageable pageable);
}
