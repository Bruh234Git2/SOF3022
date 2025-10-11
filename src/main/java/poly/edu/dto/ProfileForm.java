package poly.edu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileForm {
    @NotBlank(message = "Vui lòng nhập họ tên")
    private String fullName;
    private String phone;
    private String address;
    @Past(message = "Ngày sinh phải trong quá khứ")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
}
