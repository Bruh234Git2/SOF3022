package poly.edu.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dto.CategoryDTO;
import poly.edu.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    // TC008: Tạo sản phẩm mới thành công
    @Test
    void testCreateProduct_Success() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Test Product");
        dto.setPrice(new BigDecimal("100000"));
        ProductDTO result = productService.createProduct(dto);
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }

    // TC009: Lấy sản phẩm theo ID tồn tại
    @Test
    void testGetProductById_Found() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Find Me Product");
        dto.setPrice(new BigDecimal("150000"));
        ProductDTO created = productService.createProduct(dto);
        ProductDTO found = productService.getProductById(created.getId());
        assertNotNull(found);
    }

    // TC010: Tìm kiếm sản phẩm theo từ khóa
    @Test
    void testSearchProducts_ByKeyword() {
        ProductDTO p1 = new ProductDTO();
        p1.setName("Apple iPhone");
        p1.setPrice(new BigDecimal("25000000"));
        productService.createProduct(p1);

        ProductDTO p2 = new ProductDTO();
        p2.setName("Samsung Galaxy");
        p2.setPrice(new BigDecimal("20000000"));
        productService.createProduct(p2);

        List<ProductDTO> results = productService.searchProducts("iphone");
        assertEquals(1, results.size());
        assertEquals("Apple iPhone", results.get(0).getName());
    }

    // TC011: Cập nhật sản phẩm thành công
    @Test
    void testUpdateProduct_Success() {
        ProductDTO created = productService.createProduct(
                new ProductDTO() {{
                    setName("Old Phone");
                    setPrice(new BigDecimal("500000"));
                }}
        );

        ProductDTO updateDto = new ProductDTO();
        updateDto.setName("New Phone");
        updateDto.setPrice(new BigDecimal("600000"));

        ProductDTO updated = productService.updateProduct(created.getId(), updateDto);
        assertEquals("New Phone", updated.getName());
        assertEquals(0, new BigDecimal("600000").compareTo(updated.getPrice()));
    }
    
    // TC012: Gán sản phẩm vào danh mục
    @Test
    void testUpdateProduct_AssignCategory() {
        CategoryDTO cat = categoryService.createCategory(new CategoryDTO() {{ setName("Electronics"); }});
        ProductDTO product = productService.createProduct(new ProductDTO() {{ setName("Laptop"); setPrice(new BigDecimal("15000000")); }});
        
        product.setCategoryId(cat.getId());
        ProductDTO updated = productService.updateProduct(product.getId(), product);
        
        assertEquals("Electronics", updated.getCategoryName());
    }

    // TC013: Xóa sản phẩm thành công
    @Test
    void testDeleteProduct_Success() {
        ProductDTO created = productService.createProduct(
                new ProductDTO() {{
                    setName("Disposable Product");
                    setPrice(new BigDecimal("10000"));
                }}
        );
        boolean deleted = productService.deleteProduct(created.getId());
        assertTrue(deleted);
        assertNull(productService.getProductById(created.getId()));
    }

    // TC014: Tìm kiếm với từ khóa rỗng, trả về tất cả sản phẩm
    @Test
    void testSearchProducts_EmptyKeyword() {
        List<ProductDTO> all = productService.getAllProducts();
        List<ProductDTO> searchResult = productService.searchProducts("");
        assertEquals(all.size(), searchResult.size());
    }
}