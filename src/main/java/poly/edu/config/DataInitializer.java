package poly.edu.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import poly.edu.entity.Account;
import poly.edu.entity.Role;
import poly.edu.repository.AccountRepository;
import poly.edu.repository.RoleRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepo, AccountRepository accRepo, PasswordEncoder encoder){
        return args -> {
            // Ensure roles exist
            roleRepo.findByName("ADMIN").orElseGet(() -> {
                Role r = new Role(); r.setName("ADMIN"); return roleRepo.save(r);
            });
            roleRepo.findByName("USER").orElseGet(() -> {
                Role r = new Role(); r.setName("USER"); return roleRepo.save(r);
            });

            // Encode plain text passwords if any
            for(Account a : accRepo.findAll()){
                String pw = a.getPassword();
                if(pw != null && !pw.startsWith("$2a$") && !pw.startsWith("$2b$") && !pw.startsWith("$2y$")){
                    a.setPassword(encoder.encode(pw));
                    accRepo.save(a);
                }
            }
        };
    }
}
