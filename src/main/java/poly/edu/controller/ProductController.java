package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.entity.Category;
import poly.edu.entity.Product;
import poly.edu.entity.ProductImage;
import poly.edu.entity.ProductReview;
import poly.edu.repository.CategoryRepository;
import poly.edu.repository.ProductImageRepository;
import poly.edu.repository.ProductRepository;
import poly.edu.repository.ProductReviewRepository;
import poly.edu.service.CartService;
import poly.edu.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductReviewRepository productReviewRepository;
    private final CartService cartService;

    @GetMapping("/products")
    public String productList(
            @RequestParam Map<String,String> params,
            Model model
    ){
        Map<String,String> p = new HashMap<>(params);
        // defaults
        p.putIfAbsent("page", "0");
        p.putIfAbsent("size", "12");
        p.putIfAbsent("sort", "popular");

        Page<Product> page = productService.search(p);
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("page", page);
        model.addAttribute("items", page.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("params", p);
        return "pages/product-list";
    }

    @GetMapping("/product/detail/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model){
        Product product = productRepository.findById(id).orElse(null);
        if(product == null){
            return "redirect:/products";
        }
        List<ProductImage> images = productImageRepository.findByProduct_Id(id);
        List<ProductReview> reviews = productReviewRepository.findByProduct_IdOrderByCreatedAtDesc(id);

        double avg = 0.0;
        if(reviews != null && !reviews.isEmpty()){
            avg = reviews.stream()
                    .filter(r -> r.getRating()!=null)
                    .mapToInt(ProductReview::getRating)
                    .average().orElse(0.0);
        }
        model.addAttribute("product", product);
        model.addAttribute("images", images);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avg);
        model.addAttribute("reviewCount", reviews==null?0:reviews.size());
        return "pages/product-detail";
    }

    @PostMapping("/product/add-to-cart")
    public String addToCart(
            @RequestParam("productId") Integer productId,
            @RequestParam(value = "qty", defaultValue = "1") Integer qty,
            @RequestParam(value = "color", defaultValue = "Default") String color,
            @RequestParam(value = "size", defaultValue = "M") String size,
            RedirectAttributes ra
    ){
        try {
            cartService.addToCart(productId, qty, color, size);
            ra.addFlashAttribute("message", "Đã thêm sản phẩm vào giỏ hàng!");
            ra.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("message", "Không thể thêm vào giỏ hàng: " + e.getMessage());
            ra.addFlashAttribute("messageType", "error");
            return "redirect:/product/detail/" + productId;
        }
        return "redirect:/pages/cart";
    }
}
