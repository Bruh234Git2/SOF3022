package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Entity đại diện cho đơn hàng trong hệ thống
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // ID tự tăng

    @ManyToOne // Nhiều đơn hàng thuộc về một tài khoản
    @JoinColumn(name = "account_id")
    private Account account; // Tài khoản đặt hàng

    @Column(name = "order_date")
    private LocalDateTime orderDate; // Ngày đặt hàng

    @Column(length = 20)
    private String status; // Trạng thái đơn hàng (PENDING, SHIPPING, COMPLETED, CANCELED)

    @Column(name = "total_amount")
    private BigDecimal totalAmount; // Tổng tiền đơn hàng

    @Column(name = "receiver_name")
    private String receiverName; // Tên người nhận

    @Column(name = "receiver_phone")
    private String receiverPhone; // Số điện thoại người nhận

    @Column(name = "receiver_address")
    private String receiverAddress; // Địa chỉ người nhận

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails; // Danh sách chi tiết đơn hàng
    
    // Annotation này sẽ gọi hàm ngay trước khi entity được lưu lần đầu
    @PrePersist
    public void prePersist() {
        orderDate = LocalDateTime.now(); // Tự động gán ngày giờ hiện tại
    }
}
