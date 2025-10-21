package poly.edu.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String roleName;
    private String status;
}
