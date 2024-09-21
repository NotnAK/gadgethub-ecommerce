package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.BrandDTO;
import com.example.ecommerce.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDTO toDTO(Brand brand);
    Brand toEntity(BrandDTO brandDTO);
}