package poly.edu.dto;

import lombok.Getter;
import lombok.Setter;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO (Data Transfer Object) dùng để truyền dữ liệu đơn hàng giữa các layer
@Getter
@Setter
@Data
public class OrderDTO {
	private Integer id; // ID đơn hàng
    private LocalDateTime orderDate; // Ngày đặt hàng
    private String status; // Trạng thái đơn hàng
    private BigDecimal totalAmount; // Tổng tiền
    private String receiverName; // Tên người nhận
    private String receiverPhone; // Số điện thoại người nhận
    private String receiverAddress; // Địa chỉ người nhận
    private Integer accountId; // ID tài khoản
    private String customerName; // Tên khách hàng
}
