package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
<<<<<<< HEAD
import java.math.BigDecimal;

/**
 * Entity đại diện cho sản phẩm
 * Mapping với bảng Products trong database
 */
=======

import java.math.BigDecimal;

>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
@Getter
@Setter
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
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
=======
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(precision = 5, scale = 2)
    private BigDecimal discount; // % giảm giá 0..100

    @Column(length = 255)
    private String image;

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Transient
    public BigDecimal getSalePrice(){
        if(discount == null) return price;
        return price.subtract(price.multiply(discount).divide(new BigDecimal("100")));
    }
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
}
