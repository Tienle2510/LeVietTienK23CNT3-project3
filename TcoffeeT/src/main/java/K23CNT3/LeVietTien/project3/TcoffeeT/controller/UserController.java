package K23CNT3.LeVietTien.project3.TcoffeeT.controller;

import K23CNT3.LeVietTien.project3.TcoffeeT.model.User;
import K23CNT3.LeVietTien.project3.TcoffeeT.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Kiểm tra quyền admin
    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("user") != null &&
                "ADMIN".equals(session.getAttribute("role"));
    }

    // ========== DANH SÁCH NGƯỜI DÙNG ==========
    @GetMapping
    public String listUsers(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập với quyền Admin!");
            return "redirect:/login";
        }

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list"; // ← QUAN TRỌNG: trỏ đến templates/admin/users/list.html
    }

    // ========== FORM THÊM NGƯỜI DÙNG ==========
    @GetMapping("/add")
    public String showAddUserForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập!");
            return "redirect:/login";
        }

        model.addAttribute("user", new User());
        return "admin/users/add"; // ← Trỏ đến templates/admin/users/add.html
    }

    // ========== LƯU NGƯỜI DÙNG ==========
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập!");
            return "redirect:/login";
        }

        try {
            if (user.getId() == null) {
                userService.createUser(user);
                redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");
            } else {
                userService.updateUser(user.getId(), user);
                redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // ========== FORM SỬA NGƯỜI DÙNG ==========
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập!");
            return "redirect:/login";
        }

        User user = userService.getUserById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "admin/users/edit"; // ← Trỏ đến templates/admin/users/edit.html
    }

    // ========== CẬP NHẬT NGƯỜI DÙNG ==========
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User user,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập!");
            return "redirect:/login";
        }

        try {
            user.setId(id);
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // ========== XÓA NGƯỜI DÙNG ==========
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập!");
            return "redirect:/login";
        }

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // ========== THAY ĐỔI TRẠNG THÁI NGƯỜI DÙNG ==========
    @GetMapping("/status/{id}")
    public String changeUserStatus(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập!");
            return "redirect:/login";
        }

        try {
            User user = userService.getUserById(id);
            if (user != null) {
                // Tạo user mới chỉ chứa trạng thái để update
                User statusUpdate = new User();
                statusUpdate.setIsActive(user.getIsActive() == null || !user.getIsActive());
                userService.updateUser(id, statusUpdate);

                String status = statusUpdate.getIsActive() ? "kích hoạt" : "vô hiệu hóa";
                redirectAttributes.addFlashAttribute("success", "Đã " + status + " người dùng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // ========== CHI TIẾT NGƯỜI DÙNG ==========
    @GetMapping("/detail/{id}")
    public String userDetail(@PathVariable Long id,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập!");
            return "redirect:/login";
        }

        User user = userService.getUserById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "admin/users/detail"; // ← QUAN TRỌNG: trỏ đến templates/admin/users/detail.html
    }
}