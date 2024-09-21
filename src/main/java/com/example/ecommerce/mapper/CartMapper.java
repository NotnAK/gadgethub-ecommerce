package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public abstract class CartMapper {

    @Autowired
    private CustomerRepository customerRepository;

    @Mapping(source = "customer.id", target = "customerId")
    public abstract CartDTO toDTO(Cart cart);

    @Mapping(source = "customerId", target = "customer", qualifiedByName = "mapCustomerIdToCustomer")
    public abstract Cart toEntity(CartDTO cartDTO);

    @Named("mapCustomerIdToCustomer")
    protected Customer mapCustomerIdToCustomer(Integer customerId) {
        if (customerId == null) {
            return null;
        }
        // Use repository to get Customer entity by ID
        return customerRepository.findById(customerId).orElse(null);
    }

}