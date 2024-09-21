package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
    Category toEntity(CategoryDTO categoryDTO);
}