package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
}
