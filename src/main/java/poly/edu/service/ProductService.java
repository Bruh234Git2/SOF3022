package poly.edu.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

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
            // gender filter using column Products.gender (Nam | Nữ | Unisex)
            String gender = trim(params.get("gender"));
            if(gender != null){
                String low = gender.toLowerCase();
                String canon = null;
                if("male".equals(low) || "nam".equals(low)) canon = "Nam";
                else if("female".equals(low) || "nu".equals(low) || "nữ".equals(low)) canon = "Nữ";
                else if("unisex".equals(low)) canon = "Unisex";
                if(canon != null){
                    ps.add(cb.equal(root.get("gender"), canon));
                }
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
    }
}
