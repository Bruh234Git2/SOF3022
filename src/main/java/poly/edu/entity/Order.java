package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity đại diện cho đơn hàng
 * Mapping với bảng Orders trong database
 */
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Mã đơn hàng (tự động tăng)

    @ManyToOne // Nhiều đơn hàng của 1 khách hàng
    @JoinColumn(name = "account_id")
    private Account account; // Khách hàng đặt hàng

    @Column(name = "order_date")
    private LocalDateTime orderDate; // Ngày đặt hàng

    @Column(length = 20)
    private String status; // Trạng thái: PENDING, SHIPPING, COMPLETED, CANCELED

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount; // Tổng tiền đơn hàng

    @Column(name = "receiver_name", length = 100)
    private String receiverName; // Tên người nhận

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone; // SĐT người nhận

    @Column(name = "receiver_address", length = 255)
    private String receiverAddress; // Địa chỉ giao hàng

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 1 đơn hàng có nhiều chi tiết
    private List<OrderDetail> orderDetails; // Danh sách sản phẩm trong đơn

    @PrePersist // Tự động set giá trị trước khi lưu
    public void prePersist() {
        if (orderDate == null) orderDate = LocalDateTime.now(); // Set ngày hiện tại
        if (status == null) status = "PENDING"; // Mặc định trạng thái CHỜ XÁC NHẬN
    }
}
