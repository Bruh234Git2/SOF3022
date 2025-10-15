package poly.edu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Integer id;
    private Integer accountId;
    private String customerName;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
}
