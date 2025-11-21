package poly.edu.dto;

import lombok.Getter;
import lombok.Setter;
import poly.edu.entity.Cart;
import poly.edu.entity.Product;

@Getter
@Setter
public class CartResponseDTO {
    private Integer id;
    private Integer quantity;
    private String color;
    private String size;
    private ProductInfo product;
    
    @Getter
    @Setter
    public static class ProductInfo {
        private Integer id;
        private String name;
        private String image;
        private Double price;
        private Double salePrice;
        private String sku;
    }
    
    // Constructor để convert từ Cart entity
    public CartResponseDTO(Cart cart) {
        this.id = cart.getId();
        this.quantity = cart.getQuantity();
        this.color = cart.getColor();
        this.size = cart.getSize();
        
        if (cart.getProduct() != null) {
            Product prod = cart.getProduct();
            this.product = new ProductInfo();
            this.product.setId(prod.getId());
            this.product.setName(prod.getName());
            this.product.setImage(prod.getImage());
            this.product.setPrice(prod.getPrice() != null ? prod.getPrice().doubleValue() : 0.0);
            this.product.setSalePrice(prod.getSalePrice() != null ? prod.getSalePrice().doubleValue() : 0.0);
            this.product.setSku(prod.getSku());
        }
    }
}
