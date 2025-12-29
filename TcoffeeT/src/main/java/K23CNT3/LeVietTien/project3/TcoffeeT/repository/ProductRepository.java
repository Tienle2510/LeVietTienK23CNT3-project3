package K23CNT3.LeVietTien.project3.TcoffeeT.repository;  // <-- ĐÚNG package

import K23CNT3.LeVietTien.project3.TcoffeeT.model.Product;  // <-- Import đúng
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsAvailableTrue();
    List<Product> findByCategoryId(Long categoryId);
}