package K23CNT3.LeVietTien.project3.onlycoffee.repository;

import K23CNT3.LeVietTien.project3.onlycoffee.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByParentIsNull();

    List<Category> findByParentIsNullAndIsActiveTrue();

    List<Category> findByIsActiveTrue();

    List<Category> findByParentId(Long parentId);

    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findAllActiveOrdered();

    boolean existsBySlug(String slug);
}