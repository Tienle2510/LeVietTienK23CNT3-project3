package K23CNT3.LeVietTien.project3.onlycoffee.service;

import K23CNT3.LeVietTien.project3.onlycoffee.model.Product;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue();
    }

    public List<Product> getHotProducts() {
        return productRepository.findByIsHotTrue();
    }

    public List<Product> getNewProducts() {
        return productRepository.findByIsNewTrue();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(productDetails.getName());
        product.setSlug(productDetails.getSlug());
        product.setDescription(productDetails.getDescription());
        product.setShortDescription(productDetails.getShortDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());
        product.setOrigin(productDetails.getOrigin());
        product.setRoastLevel(productDetails.getRoastLevel());
        product.setWeight(productDetails.getWeight());
        product.setIsFeatured(productDetails.getIsFeatured());
        product.setIsHot(productDetails.getIsHot());
        product.setIsNew(productDetails.getIsNew());
        product.setIsActive(productDetails.getIsActive());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional
    public void updateProductQuantity(Long productId, int quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        int newQuantity = product.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }

        product.setQuantity(newQuantity);
        productRepository.save(product);
    }

    public Page<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId, Pageable pageable) {
        return productRepository.searchProducts(name, minPrice, maxPrice, categoryId, pageable);
    }

    public long countActiveProducts() {
        return productRepository.countActiveProducts();
    }

    public long getTotalStock() {
        return productRepository.getTotalStock();
    }
}