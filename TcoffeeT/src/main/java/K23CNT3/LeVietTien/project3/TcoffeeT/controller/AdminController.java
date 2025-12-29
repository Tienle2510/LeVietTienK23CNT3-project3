package K23CNT3.LeVietTien.project3.TcoffeeT.controller;

import K23CNT3.LeVietTien.project3.TcoffeeT.model.Product;
import K23CNT3.LeVietTien.project3.TcoffeeT.service.ProductService;
import K23CNT3.LeVietTien.project3.TcoffeeT.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Kiểm tra đăng nhập
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Kiểm tra quyền admin
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            model.addAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        // Lấy thống kê
        long totalProducts = productService.countProducts();
        long totalUsers = userService.countUsers();
        long activeUsers = userService.countActiveUsers();

        // Đưa dữ liệu vào model
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);

        return "admin/dashboard";
    }

    // ========== QUẢN LÝ SẢN PHẨM ==========

    // 1. DANH SÁCH SẢN PHẨM
    @GetMapping("/products")
    public String listProducts(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            model.addAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/products/list";
    }

    // 2. FORM THÊM SẢN PHẨM
    @GetMapping("/products/add")
    public String showAddProductForm(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            model.addAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        model.addAttribute("product", new Product());
        return "admin/products/add";
    }

    // 3. XỬ LÝ LƯU SẢN PHẨM (cho cả thêm và sửa)
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        try {
            // Set default values
            if (product.getIsAvailable() == null) {
                product.setIsAvailable(false);
            }
            if (product.getIsFeatured() == null) {
                product.setIsFeatured(false);
            }
            if (product.getStockQuantity() == null) {
                product.setStockQuantity(0);
            }
            if (product.getDiscountPercent() == null) {
                product.setDiscountPercent(0);
            }
            if (product.getCategoryId() == null) {
                product.setCategoryId(1L);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success",
                    product.getId() == null ?
                            "Thêm sản phẩm thành công!" :
                            "Cập nhật sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // 4. FORM SỬA SẢN PHẨM
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id,
                                      HttpSession session,
                                      Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            model.addAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        Product product = productService.getProductById(id);
        if (product == null) {
            model.addAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        return "admin/products/edit";
    }

    // 5. CHI TIẾT SẢN PHẨM
    @GetMapping("/products/detail/{id}")
    public String showProductDetail(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            model.addAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        Product product = productService.getProductById(id);
        if (product == null) {
            model.addAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        return "admin/products/detail";
    }

    // 6. XÓA SẢN PHẨM (Xác nhận)
    @GetMapping("/products/delete/{id}")
    public String showDeleteConfirmation(@PathVariable Long id,
                                         HttpSession session,
                                         Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            model.addAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        Product product = productService.getProductById(id);
        if (product == null) {
            model.addAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        return "admin/products/delete";
    }

    // 7. XÓA SẢN PHẨM (Xử lý)
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // ========== QUẢN LÝ DANH MỤC ==========
    @GetMapping("/categories")
    public String manageCategories(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            model.addAttribute("error", "Bạn không có quyền admin!");
            return "redirect:/";
        }
        return "admin/categories";
    }

    // ========== ĐĂNG XUẤT ==========
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}