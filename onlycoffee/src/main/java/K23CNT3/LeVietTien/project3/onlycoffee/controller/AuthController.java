package K23CNT3.LeVietTien.project3.onlycoffee.controller;

import K23CNT3.LeVietTien.project3.onlycoffee.dto.RegisterDTO;
import K23CNT3.LeVietTien.project3.onlycoffee.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/onlycoffee")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Đã đăng xuất thành công!");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("registerDTO") @Valid RegisterDTO registerDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(registerDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/onlycoffee/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}