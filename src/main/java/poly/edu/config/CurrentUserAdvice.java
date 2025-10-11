package poly.edu.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import poly.edu.entity.Account;
import poly.edu.repository.AccountRepository;

import java.util.Optional;

@ControllerAdvice
@Component
public class CurrentUserAdvice {

    private final AccountRepository accountRepository;

    public CurrentUserAdvice(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @ModelAttribute("currentAccount")
    public Account currentAccount(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated() || auth.getName() == null || auth.getName().equals("anonymousUser")){
            return null;
        }
        String email = auth.getName();
        Optional<Account> acc = accountRepository.findByEmail(email);
        return acc.orElse(null);
    }
}
