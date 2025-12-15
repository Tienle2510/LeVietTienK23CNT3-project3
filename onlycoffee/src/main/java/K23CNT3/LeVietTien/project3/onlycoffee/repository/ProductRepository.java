package K23CNT3.LeVietTien.project3.onlycoffee.repository;

import K23CNT3.LeVietTien.project3.onlycoffee.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByIsFeaturedTrue();

    List<Product> findByIsHotTrue();

    List<Product> findByIsNewTrue();

    List<Product> findByIsActiveTrue();

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "p.isActive = true")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    long countActiveProducts();

    @Query("SELECT SUM(p.quantity) FROM Product p WHERE p.isActive = true")
    long getTotalStock();
}