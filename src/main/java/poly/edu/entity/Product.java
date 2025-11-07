package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(precision = 5, scale = 2)
    private BigDecimal discount; // % giảm giá 0..100

    @Column(length = 255)
    private String image;
    
    @Column(name = "sku", length = 50)
    private String sku;

    @Column(name = "gender", length = 10)
    private String gender; // Nam, Nữ, Unisex

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
}
