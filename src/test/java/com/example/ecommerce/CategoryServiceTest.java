package com.example.ecommerce;

import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Test Category");

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test Category");
    }

    @Test
    public void testGetCategoryByIdSuccess() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(1);

        assertEquals(1, result.getId());
        assertEquals("Test Category", result.getName());
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryMapper, times(1)).toDTO(category);
    }

    @Test
    public void testGetCategoryByIdThrowsException() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1));
        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateCategory() {
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertEquals(categoryDTO.getId(), result.getId());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testUpdateCategorySuccess() {
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.updateCategory(categoryDTO);

        assertEquals(categoryDTO.getId(), result.getId());
        verify(categoryRepository, times(1)).existsById(1);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testUpdateCategoryThrowsException() {
        when(categoryRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryDTO));
        verify(categoryRepository, times(1)).existsById(1);
    }

    @Test
    public void testDeleteCategorySuccess() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1);

        verify(productRepository, times(1)).clearCategoryFromProducts(1);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    public void testDeleteCategoryThrowsException() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1));
        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    public void testGetAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);

        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAll();
    }
}
