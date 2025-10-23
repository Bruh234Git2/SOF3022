package poly.edu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    // Quan trọng: JavaScript phải gửi lên ID của sản phẩm
	@JsonProperty("id")
    private Integer productId; 
    private int qty;
    private String color;
    private String size;
    
    // Backend sẽ tự lấy price từ DB, không tin giá từ client
}