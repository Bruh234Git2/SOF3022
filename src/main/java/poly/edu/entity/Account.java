package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Collection; // Thêm
import java.util.Collections; // Thêm

@Getter
@Setter
@Entity
@Table(name = "Accounts")
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(length = 20)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "avatar")
    private String avatar; // URL ảnh đại diện

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "token_expiry_date")
    private LocalDateTime tokenExpiryDate;
    @PrePersist
    public void prePersist(){
        if(status == null) status = "ACTIVE";
        if(createdAt == null) createdAt = LocalDateTime.now();
        if(updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = (this.role != null && this.role.getName() != null)
                ? this.role.getName().toUpperCase()
                : "USER"; // Mặc định là USER nếu không có role
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    // `getPassword()` đã có sẵn từ @Getter

    @Override
    public String getUsername() {
        // Trả về email vì bạn đang dùng email để đăng nhập
        return this.email; 
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không bao giờ hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tài khoản không bị khóa (bạn có thể thay đổi logic này)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Mật khẩu không bao giờ hết hạn
    }

    @Override
    public boolean isEnabled() {
        // Dùng trường status để kiểm tra
        return "ACTIVE".equalsIgnoreCase(this.status);
    }
}
