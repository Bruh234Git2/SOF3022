package poly.edu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import poly.edu.entity.Account;
import poly.edu.repository.AccountRepository;
import poly.edu.service.CartService;

/**
 * GlobalControllerAdvice: Inject các thuộc tính toàn cục vào tất cả các views
 * - cartCount: Số lượng items trong giỏ hàng
 * - currentAccount: Thông tin account hiện tại
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final CartService cartService;
    private final AccountRepository accountRepository;

    /**
     * Inject cartCount vào model cho tất cả các views
     * Hiển thị badge số lượng giỏ hàng trong navbar
     */
    @ModelAttribute
    public void addCartCount(Model model) {
        try {
            int count = cartService.getCartCount();
            model.addAttribute("cartCount", count);
        } catch (Exception e) {
            model.addAttribute("cartCount", 0);
        }
    }

    /**
     * Inject currentAccount vào model cho tất cả các views
     * Dùng để hiển thị avatar và thông tin user trong navbar
     */
    @ModelAttribute
    public void addCurrentAccount(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String email = auth.getName();
                Account account = accountRepository.findByEmail(email).orElse(null);
                model.addAttribute("currentAccount", account);
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}
