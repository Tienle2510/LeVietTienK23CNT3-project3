package K23CNT3.LeVietTien.project3.onlycoffee.service;

import K23CNT3.LeVietTien.project3.onlycoffee.model.Category;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    public List<Category> getParentCategories() {
        return categoryRepository.findByParentIsNullAndIsActiveTrue();
    }

    public List<Category> getAllActiveOrdered() {
        return categoryRepository.findAllActiveOrdered();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isEmpty()) {
            // Tạo slug từ name nếu chưa có
            String slug = category.getName().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-");
            category.setSlug(slug);
        }

        // Kiểm tra slug đã tồn tại chưa
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new RuntimeException("Category slug already exists: " + category.getSlug());
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setImageUrl(categoryDetails.getImageUrl());
        category.setDisplayOrder(categoryDetails.getDisplayOrder());
        category.setIsActive(categoryDetails.getIsActive());

        // Chỉ update slug nếu name thay đổi
        if (!category.getName().equals(categoryDetails.getName())) {
            String slug = categoryDetails.getName().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-");
            category.setSlug(slug);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlug(slug);
    }
}