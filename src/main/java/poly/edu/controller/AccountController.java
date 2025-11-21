package poly.edu.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.dto.SignUpForm;
import poly.edu.dto.ProfileForm;
import poly.edu.dto.ChangePasswordForm;
import poly.edu.dto.ForgotPasswordForm;
import poly.edu.entity.Account;
import poly.edu.repository.AccountRepository;
import poly.edu.repository.ProductRepository;
import poly.edu.service.AccountService;
import poly.edu.service.FileService;
import poly.edu.service.ForgotPasswordMailService;
import poly.edu.dto.ResetPasswordForm;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.List;

import poly.edu.entity.Product;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final FileService fileService;
    private final ForgotPasswordMailService forgotPasswordMailService;
    private final ProductRepository productRepository;

//    public AccountController(AccountService accountService, FileService fileService) {
//        this.accountService = accountService;
//        this.fileService = fileService;
//    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        if(!model.containsAttribute("form")){
            model.addAttribute("form", new SignUpForm());
        }
        return "pages/sign-up";
    }

    @PostMapping("/sign-up")
    public String doSignUp(@Valid @ModelAttribute("form") SignUpForm form,
                           BindingResult binding,
                           RedirectAttributes ra){
        // validate business rules
        if(!form.getPassword().equals(form.getConfirmPassword())){
            binding.rejectValue("confirmPassword", "Mismatch", "Mật khẩu nhập lại không khớp");
        }
        if(accountService.emailExists(form.getEmail())){
            binding.rejectValue("email", "Exists", "Email đã tồn tại");
        }
        if(accountService.usernameExists(form.getUsername())){
            binding.rejectValue("username", "Exists", "Username đã tồn tại");
        }
        if(binding.hasErrors()){
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", binding);
            ra.addFlashAttribute("form", form);
            return "redirect:/account/sign-up";
        }
        Account acc = new Account();
        acc.setUsername(form.getUsername());
        acc.setFullName(form.getFullName());
        acc.setEmail(form.getEmail());
        acc.setPassword(form.getPassword());
        acc.setPhone(form.getPhone());
        acc.setAddress(form.getAddress());
        accountService.register(acc);
        ra.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/auth/login";
    }

    @GetMapping("/edit-profile")
    public String editProfileForm(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account acc = accountService.findByEmail(email);
        if(!model.containsAttribute("profile")){
            ProfileForm form = new ProfileForm();
            if(acc != null){
                form.setFullName(acc.getFullName());
                form.setPhone(acc.getPhone());
                form.setAddress(acc.getAddress());
                form.setBirthDate(acc.getBirthDate());
            }
            model.addAttribute("profile", form);
        }
        if(!model.containsAttribute("account")){
            model.addAttribute("account", acc);
        }
        return "pages/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String updateProfile(@Valid @ModelAttribute("profile") ProfileForm form,
                                BindingResult binding,
                                @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                RedirectAttributes ra) {
        if(binding.hasErrors()){
            // Keep profile and current account for the next GET render
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Account current = accountService.findByEmail(email);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.profile", binding);
            ra.addFlashAttribute("profile", form);
            ra.addFlashAttribute("account", current);
            return "redirect:/account/edit-profile";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account acc = accountService.findByEmail(email);
        if(acc == null){
            ra.addFlashAttribute("error", "Không tìm thấy tài khoản");
            return "redirect:/auth/login";
        }
        String avatarUrl = null;
        try {
            if(avatar != null && !avatar.isEmpty()){
                avatarUrl = fileService.save(avatar);
            }
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Tải ảnh thất bại: " + ex.getMessage());
            return "redirect:/account/edit-profile";
        }
        Account updated = accountService.updateProfile(acc, form.getFullName(), form.getPhone(), form.getAddress(), form.getBirthDate(), avatarUrl);
        // Flash back updated account so avatar shows immediately after redirect
        ra.addFlashAttribute("account", updated);
        ra.addFlashAttribute("success", "Cập nhật hồ sơ thành công");
        return "redirect:/account/edit-profile";
    }
 // ----------------- Đổi mật khẩu -----------------
    @GetMapping("/change-password")
    public String showChangePassword(Model model) {
        // Nếu model chưa có "form", thêm một form mới.
        // Điều này giúp giữ lại dữ liệu form khi có lỗi redirect về
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ChangePasswordForm());
        }
        return "pages/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("form") ChangePasswordForm form,
                                 BindingResult result,
                                 RedirectAttributes ra) { // Sử dụng RedirectAttributes
        // 1. Kiểm tra lỗi validation cơ bản
        if (result.hasErrors()) {
            // Gửi lại form và lỗi qua redirect
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", result);
            ra.addFlashAttribute("form", form);
            return "redirect:/account/change-password";
        }

        // 2. Lấy thông tin tài khoản
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Lấy email (đã sửa)
        Account acc = accountRepository.findByEmail(email).orElse(null);

        if (acc == null) {
            ra.addFlashAttribute("error", "Lỗi: Không tìm thấy tài khoản. Vui lòng đăng nhập lại.");
            return "redirect:/auth/login";
        }
        
        // 3. Dùng try-catch để xử lý logic từ service
        try {
            accountService.changePassword(acc, form.getCurrentPassword(), form.getNewPassword(), form.getConfirmPassword());
            ra.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            // Bắt lỗi nghiệp vụ từ service và gửi thông báo
            ra.addFlashAttribute("error", e.getMessage());
            // Gửi lại dữ liệu form đã nhập để người dùng không phải gõ lại
            ra.addFlashAttribute("form", form);
        }

        return "redirect:/account/change-password";
    }

    // ----------------- Quên mật khẩu -----------------
    @GetMapping("/forgot-password")
    public String showForgotPassword(Model model) {
        // Giữ nguyên, thêm form rỗng nếu chưa có
        if (!model.containsAttribute("form")) {
             model.addAttribute("form", new ForgotPasswordForm());
        }
        return "pages/forgot-password";
    }

    /**
     * SỬA LẠI PHƯƠNG THỨC NÀY
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute("form") ForgotPasswordForm form,
                                 BindingResult result, 
                                 Model model,
                                 RedirectAttributes ra) throws MessagingException { // Thêm RedirectAttributes
        if (result.hasErrors()) {
            return "pages/forgot-password";
        }

        Account acc = accountService.findByEmail(form.getEmail());
        if (acc == null) {
            model.addAttribute("error", "Email chưa được đăng ký!");
            return "pages/forgot-password";
        }

        // 1. Tạo và lưu token vào DB
        String token = accountService.createPasswordResetToken(acc);

        // 2. Tạo URL đầy đủ (ví dụ: http://localhost:8080/account/reset-password?token=...)
        String resetUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/account/reset-password")
                .queryParam("token", token)
                .toUriString();

        // 3. Gửi email với URL này
        // (Bạn cần sửa mailService để chấp nhận `resetUrl`)
        forgotPasswordMailService.sendResetLink(form.getEmail(), resetUrl); 
        // Giả sử sendResetLink(email, url)

        ra.addFlashAttribute("success", "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn!");
        return "redirect:/account/forgot-password"; // Dùng redirect để tránh F5
    }

    // ----- THÊM 2 PHƯƠNG THỨC MỚI NÀY VÀO -----

    /**
     * GET /account/reset-password
     * Hiển thị form reset khi người dùng click link từ email
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes ra) {
        // 1. Xác thực token
        Account acc = accountService.validatePasswordResetToken(token);
        
        if (acc == null) {
            // Token không hợp lệ hoặc hết hạn
            ra.addFlashAttribute("error", "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "redirect:/account/forgot-password"; // Đưa về trang quên mật khẩu
        }

        // 2. Token hợp lệ, chuẩn bị form
        ResetPasswordForm form = new ResetPasswordForm();
        form.setToken(token); // Gắn token vào form để gửi đi khi POST
        model.addAttribute("form", form);
        
        return "pages/reset-password"; // Trả về trang HTML của bạn
    }

    /**
     * POST /account/reset-password
     * Xử lý việc lưu mật khẩu mới
     */
    @PostMapping("/reset-password")
    public String handleResetPassword(@Valid @ModelAttribute("form") ResetPasswordForm form,
                                      BindingResult result,
                                      RedirectAttributes ra,
                                      Model model) {
        // 1. Kiểm tra validation cơ bản (@NotBlank, @Size)
        if (result.hasErrors()) {
            // Trả về token để form không bị lỗi (do trường `token` là `hidden`)
            model.addAttribute("token", form.getToken());
            return "pages/reset-password";
        }

        // 2. Xác thực lại token lần nữa để đảm bảo an toàn
        Account acc = accountService.validatePasswordResetToken(form.getToken());
        if (acc == null) {
            ra.addFlashAttribute("error", "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "redirect:/account/forgot-password";
        }

        // 3. Mọi thứ OK, tiến hành đổi mật khẩu
        try {
            accountService.completePasswordReset(acc, form.getNewPassword(), form.getConfirmPassword());
        } catch (IllegalArgumentException e) {
            // Bắt lỗi (ví dụ: mật khẩu không khớp)
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", form.getToken());
            return "pages/reset-password";
        }
        
        ra.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
        return "redirect:/auth/login"; // Chuyển về trang đăng nhập
    }
  
}
