package poly.edu.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.edu.entity.Account;
import poly.edu.entity.Role;
import poly.edu.repository.AccountRepository;
import poly.edu.repository.RoleRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
}
