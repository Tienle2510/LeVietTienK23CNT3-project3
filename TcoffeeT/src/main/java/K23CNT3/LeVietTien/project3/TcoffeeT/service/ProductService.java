package K23CNT3.LeVietTien.project3.TcoffeeT.service;

import K23CNT3.LeVietTien.project3.TcoffeeT.model.Product;
import K23CNT3.LeVietTien.project3.TcoffeeT.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    public List<Product> getFeaturedProducts() {
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(p -> p.getIsFeatured() != null && p.getIsFeatured())
                .filter(p -> p.getIsAvailable() != null && p.getIsAvailable())
                .toList();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public long countProducts() {
        return productRepository.count();
    }

    public long countAvailableProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(p -> p.getIsAvailable() != null && p.getIsAvailable())
                .count();
    }
}