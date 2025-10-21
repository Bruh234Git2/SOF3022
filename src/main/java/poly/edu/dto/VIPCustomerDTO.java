package poly.edu.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VIPCustomerDTO {
    private String customerName;
    private BigDecimal totalSpent;
}
