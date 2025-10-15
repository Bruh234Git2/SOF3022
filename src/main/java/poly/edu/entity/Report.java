package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revenue_id")
    private Integer revenueId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "total_sales", precision = 18, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "min_price", precision = 18, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 18, scale = 2)
    private BigDecimal maxPrice;

    @Column(name = "avg_price", precision = 18, scale = 2)
    private BigDecimal avgPrice;
}
