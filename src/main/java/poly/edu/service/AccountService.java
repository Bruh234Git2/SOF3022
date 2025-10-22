package poly.edu.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.edu.entity.Account;
import poly.edu.entity.Role;
import poly.edu.repository.AccountRepository;
import poly.edu.repository.RoleRepository;
import java.time.LocalDateTime;
import java.util.UUID; 

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int TOKEN_EXPIRATION_MINUTES = 15;

    public AccountService(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email){
        return accountRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username){
        return accountRepository.existsByUsername(username);
    }

    @Transactional
    public Account register(@Valid Account acc){
        Role roleUser = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("ROLE USER chưa tồn tại"));
        acc.setRole(roleUser);
        acc.setPassword(passwordEncoder.encode(acc.getPassword()));
        return accountRepository.save(acc);
    }

    public Account findByEmail(String email){
        return accountRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public Account updateProfile(Account acc, String fullName, String phone, String address, java.time.LocalDate birthDate, String avatarUrl){
        if(fullName != null) acc.setFullName(fullName);
        acc.setPhone(phone);
        acc.setAddress(address);
        acc.setBirthDate(birthDate);
        if(avatarUrl != null && !avatarUrl.isBlank()){
            acc.setAvatar(avatarUrl);
        }
        return accountRepository.save(acc);
    }
    @Transactional
    public void changePassword(Account acc, String currentPassword, String newPassword, String confirmPassword) {
        // 1. Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp không
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp.");
        }

        // 2. Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, acc.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }

        // 3. Mã hóa và cập nhật mật khẩu mới
        acc.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(acc);
    }

    @Transactional
    public boolean resetPasswordByEmail(String email, String newPassword) {
        Account acc = accountRepository.findByEmail(email).orElse(null);
        if (acc == null) return false;
        acc.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(acc);
        return true;
    }
    @Transactional
    public String createPasswordResetToken(Account acc) {
        String token = UUID.randomUUID().toString();
        acc.setResetPasswordToken(token);
        // Đặt thời gian hết hạn (ví dụ: 15 phút từ bây giờ)
        acc.setTokenExpiryDate(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES));
        accountRepository.save(acc);
        return token;
    }

    /**
     * Xác thực token. Trả về Account nếu token hợp lệ và chưa hết hạn.
     */
    public Account validatePasswordResetToken(String token) {
        Account acc = accountRepository.findByResetPasswordToken(token)
            .orElse(null);

        if (acc == null) {
            return null; // Không tìm thấy token
        }

        // Kiểm tra token hết hạn
        if (acc.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            // Xóa token cũ nếu hết hạn
            acc.setResetPasswordToken(null);
            acc.setTokenExpiryDate(null);
            accountRepository.save(acc);
            return null; // Coi như không hợp lệ
        }

        return acc; // Token hợp lệ
    }

    /**
     * Hoàn tất việc reset mật khẩu và xóa token
     */
    @Transactional
    public void completePasswordReset(Account acc, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp.");
        }
        
        acc.setPassword(passwordEncoder.encode(newPassword));
        // Xóa token sau khi đã dùng
        acc.setResetPasswordToken(null);
        acc.setTokenExpiryDate(null);
        accountRepository.save(acc);
    }
}
