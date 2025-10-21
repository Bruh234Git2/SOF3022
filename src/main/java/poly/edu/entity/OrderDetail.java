package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
<<<<<<< HEAD
=======

>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "OrderDetails")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id")
<<<<<<< HEAD
    private Order order;
=======
    private poly.edu.entity.Order order;
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

<<<<<<< HEAD
    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 18, scale = 2, nullable = false)
=======
    private Integer quantity;

>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
    private BigDecimal price;
}
