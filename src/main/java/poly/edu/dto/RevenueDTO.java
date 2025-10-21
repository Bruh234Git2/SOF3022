package poly.edu.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RevenueDTO {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
}
