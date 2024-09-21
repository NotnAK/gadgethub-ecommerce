package com.example.ecommerce.service;

import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productRepository = productRepository;
    }


    /**
     * Retrieves a category by its ID.
     *
     * @param id The ID of the category to retrieve.
     * @return A CategoryDTO object containing category information.
     * @throws ResourceNotFoundException if the category with the given ID is not found.
     */
    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return categoryMapper.toDTO(category);
    }
    /**
     * Creates a new category.
     *
     * @param categoryDTO The CategoryDTO object containing information about the category to create.
     * @return A CategoryDTO object containing the created category's information.
     */
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }
    /**
     * Updates an existing category.
     *
     * @param categoryDTO The CategoryDTO object containing updated information about the category.
     * @return A CategoryDTO object containing the updated category's information.
     * @throws ResourceNotFoundException if the category with the given ID is not found.
     */
    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        if (!categoryRepository.existsById(categoryDTO.getId())) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryDTO.getId());
        }
        Category category = categoryMapper.toEntity(categoryDTO);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }
    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @throws ResourceNotFoundException if the category with the given ID is not found.
     */
    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        productRepository.clearCategoryFromProducts(id);
        categoryRepository.delete(category);
    }
    /**
     * Retrieves a list of all available categories.
     *
     * @return A list of CategoryDTO objects containing information about all categories.
     */
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}
