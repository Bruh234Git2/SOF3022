package poly.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poly.edu.entity.Product;

// DTO đại diện cho sản phẩm đã mua của khách hàng
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedItem {
    private Product product; // Sản phẩm
    private Long totalQty; // Tổng số lượng đã mua
}
