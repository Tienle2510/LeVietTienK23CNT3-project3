package K23CNT3.LeVietTien.project3.TcoffeeT.controller;

import K23CNT3.LeVietTien.project3.TcoffeeT.model.Product;
import K23CNT3.LeVietTien.project3.TcoffeeT.model.User;
import K23CNT3.LeVietTien.project3.TcoffeeT.service.ProductService;
import K23CNT3.LeVietTien.project3.TcoffeeT.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@SuppressWarnings("unused")
public class HomeController {

    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public HomeController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    // ========== TRANG CHỦ ==========
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Product> allProducts = productService.getAvailableProducts();

        List<Product> featuredProducts = allProducts.stream()
                .filter(p -> p.getIsFeatured() != null && p.getIsFeatured())
                .limit(8)
                .toList();

        List<Product> coffeeProducts = allProducts.stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId() == 1)
                .limit(4)
                .toList();

        List<Product> discountProducts = allProducts.stream()
                .filter(p -> p.getDiscountPercent() != null && p.getDiscountPercent() > 0)
                .limit(4)
                .toList();

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("coffeeProducts", coffeeProducts);
        model.addAttribute("discountProducts", discountProducts);
        model.addAttribute("totalProducts", allProducts.size());

        model.addAttribute("storeName", "Coffee Elegance");
        model.addAttribute("storePhone", "0987 654 321");
        model.addAttribute("storeAddress", "123 Đường ABC, Quận 1, TP.HCM");
        model.addAttribute("storeHours", "7:00 - 22:00 hàng ngày");

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "home";
    }

    // ========== TRANG MENU ==========
    @GetMapping("/menu")
    public String menu(Model model, HttpSession session) {
        List<Product> allProducts = productService.getAvailableProducts();

        List<Product> coffeeProducts = allProducts.stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId() == 1)
                .toList();

        List<Product> teaProducts = allProducts.stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId() == 2)
                .toList();

        List<Product> juiceProducts = allProducts.stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId() == 3)
                .toList();

        List<Product> cakeProducts = allProducts.stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId() == 4)
                .toList();

        model.addAttribute("coffeeProducts", coffeeProducts);
        model.addAttribute("teaProducts", teaProducts);
        model.addAttribute("juiceProducts", juiceProducts);
        model.addAttribute("cakeProducts", cakeProducts);
        model.addAttribute("totalProducts", allProducts.size());

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "menu";
    }

    // ========== CHI TIẾT SẢN PHẨM ==========
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productService.getProductById(id);

        if (product == null || (product.getIsAvailable() != null && !product.getIsAvailable())) {
            model.addAttribute("error", "Sản phẩm không tồn tại hoặc đã ngừng bán");
            return "error";
        }

        List<Product> relatedProducts = productService.getAvailableProducts().stream()
                .filter(p -> p.getCategoryId() != null &&
                        p.getCategoryId().equals(product.getCategoryId()) &&
                        !p.getId().equals(id))
                .limit(4)
                .toList();

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "product-detail";
    }

    // ========== GIỚI THIỆU ==========
    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        model.addAttribute("title", "Giới thiệu Coffee Elegance");
        model.addAttribute("pageTitle", "Về chúng tôi");

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "about";
    }

    // ========== LIÊN HỆ ==========
    @GetMapping("/contact")
    public String contact(Model model, HttpSession session) {
        model.addAttribute("title", "Liên hệ Coffee Elegance");
        model.addAttribute("pageTitle", "Liên hệ");

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "contact";
    }

    // ========== ĐĂNG NHẬP ==========
    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("user") != null) {
            String role = (String) session.getAttribute("role");
            if ("ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }
        }

        model.addAttribute("title", "Đăng nhập");
        model.addAttribute("pageTitle", "Đăng nhập");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        try {
            // Gọi service để xác thực - SỬA Ở ĐÂY
            User user = userService.authenticate(username, password);

            if (user != null) {
                session.setAttribute("user", user.getUsername());
                session.setAttribute("userId", user.getId());
                session.setAttribute("role", user.getRole().name());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("email", user.getEmail());

                if (user.getRole() == User.Role.ADMIN) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/";
                }
            } else {
                // Thử với tài khoản admin mặc định
                if ("admin".equals(username) && "admin123".equals(password)) {
                    session.setAttribute("user", "admin");
                    session.setAttribute("role", "ADMIN");
                    session.setAttribute("fullName", "Administrator");
                    return "redirect:/admin/dashboard";
                }

                model.addAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
                model.addAttribute("title", "Đăng nhập");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("title", "Đăng nhập");
            return "login";
        }
    }

    // ========== ĐĂNG KÝ ==========
    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }

        model.addAttribute("title", "Đăng ký tài khoản");
        model.addAttribute("pageTitle", "Đăng ký");
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           HttpSession session,
                           Model model) {

        try {
            // Kiểm tra username đã tồn tại chưa - SỬA Ở ĐÂY
            if (userService.existsByUsername(username)) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
                model.addAttribute("title", "Đăng ký");
                return "register";
            }

            // Kiểm tra email đã tồn tại chưa - SỬA Ở ĐÂY
            if (userService.existsByEmail(email)) {
                model.addAttribute("error", "Email đã được sử dụng!");
                model.addAttribute("title", "Đăng ký");
                return "register";
            }

            // Tạo user mới
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // Nên mã hóa password trong tương lai
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRole(User.Role.USER);
            newUser.setIsActive(true);

            User savedUser = userService.createUser(newUser);

            session.setAttribute("user", savedUser.getUsername());
            session.setAttribute("userId", savedUser.getId());
            session.setAttribute("role", savedUser.getRole().name());
            session.setAttribute("fullName", savedUser.getFullName());
            session.setAttribute("email", savedUser.getEmail());

            model.addAttribute("success", "Đăng ký thành công! Chào mừng " + fullName);
            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đăng ký: " + e.getMessage());
            model.addAttribute("title", "Đăng ký");
            return "register";
        }
    }

    // ========== ĐĂNG XUẤT ==========
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ========== GIỎ HÀNG ==========
    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        model.addAttribute("title", "Giỏ hàng của bạn");
        model.addAttribute("pageTitle", "Giỏ hàng");

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "cart";
    }

    // ========== TRANG NGƯỜI DÙNG ==========
    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("title", "Trang cá nhân");
        model.addAttribute("pageTitle", "Trang cá nhân");

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "user/dashboard";
    }

    // ========== API GIỎ HÀNG ==========
    @PostMapping("/api/cart/add")
    @ResponseBody
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {

        Product product = productService.getProductById(productId);
        if (product == null) {
            return "{\"success\": false, \"message\": \"Sản phẩm không tồn tại\"}";
        }

        if (product.getStockQuantity() < quantity) {
            return "{\"success\": false, \"message\": \"Số lượng vượt quá tồn kho\"}";
        }

        Integer currentCount = (Integer) session.getAttribute("cartCount");
        if (currentCount == null) {
            currentCount = 0;
        }
        session.setAttribute("cartCount", currentCount + quantity);

        return "{\"success\": true, \"message\": \"Đã thêm vào giỏ hàng\", \"count\": " + (currentCount + quantity) + "}";
    }

    // ========== TRANG LỖI ==========
    @GetMapping("/error")
    public String errorPage(Model model, HttpSession session) {
        model.addAttribute("title", "Lỗi");

        Integer cartCount = (Integer) session.getAttribute("cartCount");
        model.addAttribute("cartCount", cartCount != null ? cartCount : 0);

        return "error";
    }
}