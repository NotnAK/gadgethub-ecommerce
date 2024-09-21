package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CustomerInfoDTO;
import com.example.ecommerce.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerInfoMapper {
    CustomerInfoDTO toDTO(Customer customer);
}