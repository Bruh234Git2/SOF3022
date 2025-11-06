package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.Account;
import poly.edu.entity.Cart;
import poly.edu.entity.Product;
import poly.edu.repository.AccountRepository;
import poly.edu.repository.CartRepository;
import poly.edu.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    /**
     * Lấy account hiện tại từ SecurityContext
     */
    private Account getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        String email = auth.getName();
        return accountRepository.findByEmail(email).orElse(null);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     * Nếu đã tồn tại (cùng product + color + size) thì tăng quantity
     */
    @Transactional
    public Cart addToCart(Integer productId, Integer qty, String color, String size) {
        Account account = getCurrentAccount();
        if (account == null) {
            throw new RuntimeException("User not authenticated");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Normalize color/size
        String normalizedColor = (color == null || color.trim().isEmpty()) ? "Default" : color.trim();
        String normalizedSize = (size == null || size.trim().isEmpty()) ? "M" : size.trim();

        // Tìm cart item hiện tại
        Optional<Cart> existingCart = cartRepository.findByAccountIdAndProductIdAndColorAndSize(
                account.getId(), productId, normalizedColor, normalizedSize
        );

        Cart cart;
        if (existingCart.isPresent()) {
            // Tăng quantity
            cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + qty);
        } else {
            // Tạo mới
            cart = new Cart();
            cart.setAccount(account);
            cart.setProduct(product);
            cart.setQuantity(qty);
            cart.setColor(normalizedColor);
            cart.setSize(normalizedSize);
        }

        return cartRepository.save(cart);
    }

    /**
     * Lấy tất cả items trong giỏ hàng của user hiện tại
     */
    public List<Cart> getCartItems() {
        Account account = getCurrentAccount();
        if (account == null) {
            return List.of();
        }
        return cartRepository.findByAccountId(account.getId());
    }

    /**
     * Đếm tổng số lượng items trong giỏ hàng
     */
    public int getCartCount() {
        List<Cart> items = getCartItems();
        return items.stream().mapToInt(Cart::getQuantity).sum();
    }

    /**
     * Xóa item khỏi giỏ hàng
     */
    @Transactional
    public void removeFromCart(Integer cartId) {
        Account account = getCurrentAccount();
        if (account == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Verify ownership
        if (!cart.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        cartRepository.delete(cart);
    }

    /**
     * Cập nhật quantity của cart item
     */
    @Transactional
    public Cart updateQuantity(Integer cartId, Integer newQty) {
        Account account = getCurrentAccount();
        if (account == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Verify ownership
        if (!cart.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (newQty <= 0) {
            cartRepository.delete(cart);
            return null;
        }
        
        cart.setQuantity(newQty);
        return cartRepository.save(cart);
    }

    /**
     * Xóa toàn bộ giỏ hàng của user hiện tại
     */
    @Transactional
    public void clearCart() {
        Account account = getCurrentAccount();
        if (account == null) {
            return;
        }
        List<Cart> items = cartRepository.findByAccountId(account.getId());
        cartRepository.deleteAll(items);
    }
}
