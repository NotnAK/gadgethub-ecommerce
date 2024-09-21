package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.ProductInfoDTO;
import com.example.ecommerce.entity.Brand;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.BrandRepository;
import com.example.ecommerce.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, BrandMapper.class})
public abstract class ProductInfoMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    // Mapping from Product to ProductInfoDTO
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    public abstract ProductInfoDTO toDTO(Product product);

    // Mapping from ProductInfoDTO to Product
    @Mapping(source = "categoryName", target = "category", qualifiedByName = "mapCategoryNameToCategory")
    @Mapping(source = "brandName", target = "brand", qualifiedByName = "mapBrandNameToBrand")
    public abstract Product toEntity(ProductInfoDTO productInfoDTO);

    // Mapping a category name to a Category entity
    @Named("mapCategoryNameToCategory")
    protected Category mapCategoryNameToCategory(String categoryName) {
        if (categoryName == null) {
            return null;
        }
        return categoryRepository.findByName(categoryName).orElse(null);
    }

    // Mapping a brand name to a Brand entity
    @Named("mapBrandNameToBrand")
    protected Brand mapBrandNameToBrand(String brandName) {
        if (brandName == null) {
            return null;
        }
        return brandRepository.findByName(brandName).orElse(null);
    }
}
