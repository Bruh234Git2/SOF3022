package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
