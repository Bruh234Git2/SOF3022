package poly.edu.security;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poly.edu.entity.Account;
import poly.edu.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account acc = accountRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));
        String roleName = acc.getRole() != null ? acc.getRole().getName() : "USER";
        List<GrantedAuthority> auths = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
        boolean enabled = acc.getStatus() == null || acc.getStatus().equalsIgnoreCase("ACTIVE");
        return User.withUsername(acc.getEmail())
                .password(acc.getPassword())
                .authorities(auths)
                .disabled(!enabled)
                .build();
    }
}
