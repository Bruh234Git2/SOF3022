package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;
import poly.edu.entity.Category;

@Repository
=======
import poly.edu.entity.Category;

>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
