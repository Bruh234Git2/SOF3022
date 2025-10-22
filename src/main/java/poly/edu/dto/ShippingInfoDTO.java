package poly.edu.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingInfoDTO {
    @NotBlank(message = "Vui lòng nhập họ tên")
    private String fullName;
    @NotBlank(message = "Vui lòng nhập số điện thoại")
    private String phone;
    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ")
    private String email;
    @NotBlank(message = "Vui lòng nhập địa chỉ")
    private String address;
    private String paymentMethod;
    private String notes; // Ghi chú (nếu có)
}