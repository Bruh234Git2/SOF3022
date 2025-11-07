package poly.edu.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Integer id;
    private String name;
    private BigDecimal price;
    private BigDecimal discount;
    private String image;
    private String description;
    private Integer categoryId;
    private String categoryName;
    private String sku;
    private String gender; // Nam, Nữ, Unisex
}
