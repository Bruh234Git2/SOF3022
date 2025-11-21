package poly.edu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    // Mapping từ frontend: productId
    @JsonProperty("productId")
    private Integer productId; 
    private Integer qty;
    private String color;
    private String size;
    
    // Backend sẽ tự lấy price từ DB, không tin giá từ client
}