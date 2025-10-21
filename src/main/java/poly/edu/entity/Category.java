package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

<<<<<<< HEAD
/**
 * Entity đại diện cho danh mục sản phẩm
 * Mapping với bảng Categories trong database
 */
=======
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
@Getter
@Setter
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private Integer id; // Mã danh mục (tự động tăng)

    @Column(length = 100, nullable = false)
    private String name; // Tên danh mục (bắt buộc)

    @Column(length = 255)
    private String description; // Mô tả danh mục
=======
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
}
