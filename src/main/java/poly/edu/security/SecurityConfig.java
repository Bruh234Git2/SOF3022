package poly.edu.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF cho API admin để cho phép PUT/DELETE requests
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/admin/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/pages/admin/**", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/account/forgot-password", "/account/reset-password/**").permitAll()
                .requestMatchers("/", "/auth/**", "/account/sign-up", 
                                 "/pages/**", 
                                 "/css/**", "/images/**", "/js/**", "/uploads/**").permitAll()
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(login -> login
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/pages/home", false)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .rememberMe(rm -> rm.key("shopomg-remember-key").tokenValiditySeconds(7*24*60*60))
            .logout(lo -> lo
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/pages/home")
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
