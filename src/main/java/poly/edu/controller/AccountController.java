package poly.edu.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.dto.SignUpForm;
import poly.edu.dto.ProfileForm;
import poly.edu.entity.Account;
import poly.edu.service.AccountService;
import poly.edu.service.FileService;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final FileService fileService;

    public AccountController(AccountService accountService, FileService fileService) {
        this.accountService = accountService;
        this.fileService = fileService;
    }

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
}
