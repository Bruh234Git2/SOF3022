package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ProductImages")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

<<<<<<< HEAD
    @ManyToOne
=======
    @ManyToOne(fetch = FetchType.LAZY)
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;
}
