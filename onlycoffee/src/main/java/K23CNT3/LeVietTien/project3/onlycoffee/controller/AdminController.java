package K23CNT3.LeVietTien.project3.onlycoffee.controller;

import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import K23CNT3.LeVietTien.project3.onlycoffee.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String dashboard(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            long totalUsers = users.size();

            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("users", users.subList(0, Math.min(10, users.size())));
            return "admin/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String userManagement(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "admin/users";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/users/update-role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUserRole(
            @PathVariable Long id,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

            // Cập nhật user với role mới
            user.setRole(role);
            userService.updateUser(id, user);

            redirectAttributes.addFlashAttribute("success",
                    "Đã cập nhật role thành: " + role + " cho user: " + user.getUsername());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle-status/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String toggleUserStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

            // Đổi trạng thái
            User.Status newStatus = (user.getStatus() == User.Status.ACTIVE)
                    ? User.Status.INACTIVE
                    : User.Status.ACTIVE;

            user.setStatus(newStatus);
            userService.updateUser(id, user);

            String statusText = (newStatus == User.Status.ACTIVE) ? "kích hoạt" : "vô hiệu hóa";
            redirectAttributes.addFlashAttribute("success",
                    "Đã " + statusText + " user: " + user.getUsername());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }
}