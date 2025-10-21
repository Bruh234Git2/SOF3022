package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dto.CategoryDTO;
import poly.edu.entity.Category;
import poly.edu.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic nghiệp vụ cho danh mục sản phẩm
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // Lấy danh sách tất cả danh mục
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO) // Chuyển Entity sang DTO
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional // Đảm bảo transaction khi thao tác DB
    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return convertToDTO(categoryRepository.save(category)); // Lưu vào DB
    }

    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) return null; // Không tìm thấy
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return convertToDTO(categoryRepository.save(category)); // Cập nhật DB
    }

    @Transactional
    public boolean deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) return false; // Không tồn tại
        categoryRepository.deleteById(id); // Xóa khỏi DB
        return true;
    }

    // Chuyển đổi Entity sang DTO để trả về API
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
