package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity đại diện cho danh mục sản phẩm
 * Mapping với bảng Categories trong database
 */
@Getter
@Setter
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Mã danh mục (tự động tăng)

    @Column(length = 100, nullable = false)
    private String name; // Tên danh mục (bắt buộc)

    @Column(length = 255)
    private String description; // Mô tả danh mục
}
