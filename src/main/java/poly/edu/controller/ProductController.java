package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.edu.entity.Category;
import poly.edu.entity.Product;
import poly.edu.repository.CategoryRepository;
import poly.edu.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

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
}
