package poly.edu.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.edu.repository.CategoryRepository;
import poly.edu.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/pages")
@RequiredArgsConstructor
public class PageController {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @GetMapping({"/home", "/"})
    public String home(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("featured", productRepository.findTopRated(PageRequest.of(0, 8)));
        return "pages/home";
    }

    @GetMapping("/product-list")
    public String productList() {
        return "redirect:/products";
    }
 
    @GetMapping("/product-detail")
    public String productDetail() {
        return "pages/product-detail";
    }
 
    @GetMapping("/cart")
    public String cart() {
        return "pages/cart";
    }
 
    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }
 
    @GetMapping("/sign-up")
    public String signUp() {
        return "pages/sign-up";
    }

    @GetMapping("/edit-profile")
    public String editProfile() {
        return "pages/edit-profile";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "pages/forgot-password";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "pages/change-password";
    }

    @GetMapping("/check-out")
    public String checkout() {
        return "pages/check-out";
    }

    @GetMapping("/order-list")
    public String orderList() {
        return "pages/order-list";
    }

    @GetMapping("/order-detail")
    public String orderDetail() {
        return "pages/order-detail";
    }

    @GetMapping("/my-product-list")
    public String myProductList() {
        return "pages/my-product-list";
    }

    @GetMapping("/thank-you")
    public String thankYou() {
        return "pages/thank-you";
    }

    // Admin pages
    @GetMapping("/admin/category")
    public String adminCategory() {
        return "pages/admin/category";
    }

    @GetMapping("/admin/product")
    public String adminProduct() {
        return "pages/admin/product";
    }

    @GetMapping("/admin/order")
    public String adminOrder() {
        return "pages/admin/order";
    }

    @GetMapping("/admin/account")
    public String adminAccount() {
        return "pages/admin/account";
    }

    @GetMapping("/admin/revenue")
    public String adminRevenue() {
        return "pages/admin/revenue";
    }

    @GetMapping("/admin/vip")
    public String adminVip() {
        return "pages/admin/vip";
    }
}
