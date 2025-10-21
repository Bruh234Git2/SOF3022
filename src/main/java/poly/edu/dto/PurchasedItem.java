package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poly.edu.entity.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedItem {
    private Product product;
    private Long totalQty;
}
