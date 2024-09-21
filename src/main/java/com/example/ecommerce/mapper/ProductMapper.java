package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Brand;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.BrandRepository;
import com.example.ecommerce.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    // Mapping from Product to ProductDTO
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "brand.id", target = "brandId")
    public abstract ProductDTO toDTO(Product product);

    // Mapping from ProductDTO to Product
    @Mapping(source = "categoryId", target = "category", qualifiedByName = "mapCategoryIdToCategory")
    @Mapping(source = "brandId", target = "brand", qualifiedByName = "mapBrandIdToBrand")
    public abstract Product toEntity(ProductDTO productDTO);

    // Mapping a category identifier to a Category entity
    @Named("mapCategoryIdToCategory")
    protected Category mapCategoryIdToCategory(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        // Use repository to get Category entity by ID
        return categoryRepository.findById(categoryId).orElse(null);
    }

    // Mapping a brand identifier to the Brand entity
    @Named("mapBrandIdToBrand")
    protected Brand mapBrandIdToBrand(Integer brandId) {
        if (brandId == null) {
            return null;
        }
        return brandRepository.findById(brandId).orElse(null);
    }
}
