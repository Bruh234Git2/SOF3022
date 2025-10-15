package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * Entity đại diện cho sản phẩm
 * Mapping với bảng Products trong database
 */
@Getter
@Setter
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Mã sản phẩm (tự động tăng)

    @Column(length = 200, nullable = false)
    private String name; // Tên sản phẩm (bắt buộc)

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal price; // Giá sản phẩm (bắt buộc)

    @Column(precision = 5, scale = 2)
    private BigDecimal discount; // Phần trăm giảm giá (0-100)

    @Column(length = 255)
    private String image; // URL ảnh chính

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description; // Mô tả chi tiết sản phẩm

    @ManyToOne // Nhiều sản phẩm thuộc 1 danh mục
    @JoinColumn(name = "category_id")
    private Category category; // Danh mục của sản phẩm
}
