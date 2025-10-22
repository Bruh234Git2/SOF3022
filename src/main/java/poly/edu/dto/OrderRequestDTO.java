package poly.edu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {
    
    @Valid // Kiểm tra các validation bên trong ShippingInfoDTO
    private ShippingInfoDTO shippingInfo;

    @NotEmpty(message = "Giỏ hàng không được để trống")
    private List<CartItemDTO> cartItems;
}