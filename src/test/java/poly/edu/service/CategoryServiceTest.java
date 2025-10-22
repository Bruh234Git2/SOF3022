package poly.edu.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dto.CategoryDTO;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    // TC001: Kiểm tra tạo mới một danh mục thành công
    @Test
    void testCreateCategory_Success() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Test Category");
        dto.setDescription("This is a test category.");
        CategoryDTO result = categoryService.createCategory(dto);
        assertNotNull(result);
        assertEquals("Test Category", result.getName());
    }

    // TC002: Lấy thông tin danh mục theo ID tồn tại
    @Test
    void testGetCategoryById_Found() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Find Me");
        CategoryDTO created = categoryService.createCategory(dto);
        CategoryDTO found = categoryService.getCategoryById(created.getId());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    // TC003: Lấy thông tin danh mục theo ID không tồn tại
    @Test
    void testGetCategoryById_NotFound() {
        CategoryDTO found = categoryService.getCategoryById(9999);
        assertNull(found);
    }

    // TC004: Cập nhật thông tin danh mục thành công
    @Test
    void testUpdateCategory_Success() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Original Name");
        CategoryDTO created = categoryService.createCategory(dto);

        CategoryDTO updateDto = new CategoryDTO();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated description.");
        CategoryDTO updated = categoryService.updateCategory(created.getId(), updateDto);
        assertEquals("Updated Name", updated.getName());
    }

    // TC005: Cập nhật danh mục không tồn tại
    @Test
    void testUpdateCategory_NotFound() {
        CategoryDTO updateDto = new CategoryDTO();
        updateDto.setName("Non-existent");
        CategoryDTO updated = categoryService.updateCategory(9999, updateDto);
        assertNull(updated);
    }

    // TC006: Xóa một danh mục thành công
    @Test
    void testDeleteCategory_Success() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("To Be Deleted");
        CategoryDTO created = categoryService.createCategory(dto);
        boolean deleted = categoryService.deleteCategory(created.getId());
        assertTrue(deleted);
        assertNull(categoryService.getCategoryById(created.getId()));
    }

    // TC007: Xóa một danh mục không tồn tại
    @Test
    void testDeleteCategory_NotFound() {
        boolean deleted = categoryService.deleteCategory(9999);
        assertFalse(deleted);
    }
}