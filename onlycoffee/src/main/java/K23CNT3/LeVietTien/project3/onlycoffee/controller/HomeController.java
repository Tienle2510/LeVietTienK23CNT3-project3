package K23CNT3.LeVietTien.project3.onlycoffee.controller;

import K23CNT3.LeVietTien.project3.onlycoffee.service.CategoryService;
import K23CNT3.LeVietTien.project3.onlycoffee.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // Trang chủ (sẽ redirect từ / đến /onlycoffee)
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/onlycoffee";
    }

    // Trang chủ chính
    @GetMapping("/onlycoffee")
    public String home(Model model) {
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("hotProducts", productService.getHotProducts());
        model.addAttribute("newProducts", productService.getNewProducts());
        model.addAttribute("categories", categoryService.getParentCategories());
        return "home";
    }

    // Trang chủ với slash
    @GetMapping("/onlycoffee/")
    public String homeWithSlash(Model model) {
        return home(model);
    }

    @GetMapping("/onlycoffee/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getActiveProducts());
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "products";
    }

    @GetMapping("/onlycoffee/about")
    public String about() {
        return "about";
    }

    @GetMapping("/onlycoffee/contact")
    public String contact() {
        return "contact";
    }

    // KHÔNG CÓ login và register ở đây - đã có trong AuthController
}