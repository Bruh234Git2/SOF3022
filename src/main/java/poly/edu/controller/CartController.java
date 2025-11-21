package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.CartItemDTO;
import poly.edu.dto.CartResponseDTO;
import poly.edu.entity.Cart;
import poly.edu.service.CartService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Thêm sản phẩm vào giỏ hàng
     * POST /api/cart/items
     * Body: { "productId": 1, "qty": 2, "color": "Trắng", "size": "M" }
     * Response: { "count": 5, "message": "Added to cart" }
     */
    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO dto) {
        try {
            cartService.addToCart(dto.getProductId(), dto.getQty(), dto.getColor(), dto.getSize());
            int count = cartService.getCartCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("message", "Added to cart");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lấy số lượng items trong giỏ hàng
     * GET /api/cart/count
     * Response: { "count": 5 }
     */
    @GetMapping("/count")
    public ResponseEntity<?> getCartCount() {
        int count = cartService.getCartCount();
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy tất cả items trong giỏ hàng
     * GET /api/cart/items
     * Response: [ { "id": 1, "product": {...}, "quantity": 2, "color": "Trắng", "size": "M" }, ... ]
     */
    @GetMapping("/items")
    public ResponseEntity<?> getCartItems() {
        try {
            List<Cart> items = cartService.getCartItems();
            List<CartResponseDTO> response = items.stream()
                    .map(CartResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load cart items: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Xóa item khỏi giỏ hàng
     * DELETE /api/cart/items/{cartId}
     * Response: { "message": "Removed from cart", "count": 4 }
     */
    @DeleteMapping("/items/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Integer cartId) {
        try {
            cartService.removeFromCart(cartId);
            int count = cartService.getCartCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Removed from cart");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Cập nhật quantity của cart item
     * PUT /api/cart/items/{cartId}
     * Body: { "qty": 3 }
     * Response: { "message": "Updated", "count": 6 }
     */
    @PutMapping("/items/{cartId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Integer cartId, @RequestBody Map<String, Integer> body) {
        try {
            Integer newQty = body.get("qty");
            if (newQty == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "qty is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            cartService.updateQuantity(cartId, newQty);
            int count = cartService.getCartCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Updated");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * DELETE /api/cart/items
     * Response: { "message": "Cart cleared" }
     */
    @DeleteMapping("/items")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Debug endpoint để kiểm tra raw cart data
     * GET /api/cart/debug
     */
    @GetMapping("/debug")
    public ResponseEntity<?> debugCart() {
        try {
            List<Cart> items = cartService.getCartItems();
            Map<String, Object> debug = new HashMap<>();
            debug.put("itemCount", items.size());
            debug.put("rawItems", items);
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Debug failed: " + e.getMessage());
            error.put("stackTrace", e.toString());
            return ResponseEntity.status(500).body(error);
        }
    }
}
