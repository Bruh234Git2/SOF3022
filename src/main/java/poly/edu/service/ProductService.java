package poly.edu.service;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dto.ProductDTO;
import poly.edu.entity.Category;
import poly.edu.entity.Product;
import poly.edu.repository.CategoryRepository;
import poly.edu.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;
=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import poly.edu.entity.Category;
import poly.edu.entity.Product;
import poly.edu.repository.ProductRepository;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
<<<<<<< HEAD
    private final CategoryRepository categoryRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Integer id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        product.setImage(dto.getImage());
        product.setDescription(dto.getDescription());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        
        return convertToDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Integer id, ProductDTO dto) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return null;
        
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        product.setImage(dto.getImage());
        product.setDescription(dto.getDescription());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        
        return convertToDTO(productRepository.save(product));
    }

    @Transactional
    public boolean deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setImage(product.getImage());
        dto.setDescription(product.getDescription());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
=======

    public Page<Product> search(Map<String, String> params){
        int page = parseInt(params.get("page"), 0);
        int size = parseInt(params.get("size"), 12);
        String sort = params.getOrDefault("sort", "popular");
        Pageable pageable = PageRequest.of(page, size, toSort(sort));

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            String q = trim(params.get("q"));
            if(q != null){
                String like = "%" + q.toLowerCase() + "%";
                ps.add(cb.like(cb.lower(root.get("name")), like));
            }
            Integer catId = parseIntObj(params.get("category"));
            if(catId != null){
                ps.add(cb.equal(root.get("category").get("id"), catId));
            }
            BigDecimal min = parseDecimal(params.get("min"));
            if(min != null){
                ps.add(cb.greaterThanOrEqualTo(root.get("price"), min));
            }
            BigDecimal max = parseDecimal(params.get("max"));
            if(max != null){
                ps.add(cb.lessThanOrEqualTo(root.get("price"), max));
            }
            String onSale = params.get("sale");
            if("1".equals(onSale)){
                ps.add(cb.greaterThan(root.get("discount"), BigDecimal.ZERO));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return productRepository.findAll(spec, pageable);
    }

    private Sort toSort(String sort){
        if("priceAsc".equals(sort)) return Sort.by(Sort.Direction.ASC, "price");
        if("priceDesc".equals(sort)) return Sort.by(Sort.Direction.DESC, "price");
        if("newest".equals(sort)) return Sort.by(Sort.Direction.DESC, "id");
        return Sort.by(Sort.Direction.DESC, "id"); // popular/default
    }

    private int parseInt(String s, int def){
        try{ return Integer.parseInt(s); }catch(Exception e){ return def; }
    }
    private Integer parseIntObj(String s){
        try{ return s==null?null:Integer.parseInt(s); }catch(Exception e){ return null; }
    }
    private BigDecimal parseDecimal(String s){
        try{ return s==null?null:new BigDecimal(s); }catch(Exception e){ return null; }
    }
    private String trim(String s){
        if(s==null) return null; s = s.trim(); return s.isEmpty()?null:s;
>>>>>>> 139d1abea9813b1401816e33e8fc94bf013419fe
    }
}
