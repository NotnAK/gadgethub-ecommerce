package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO customerDTO);
}
