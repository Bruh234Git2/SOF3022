package poly.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pages")
public class PageController {

    @GetMapping({"/home", "/"})
    public String home() {
        return "pages/home";
    }

    @GetMapping("/product-list")
    public String productList() {
        return "pages/product-list";
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
