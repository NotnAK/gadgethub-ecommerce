package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.repository.CustomerRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public abstract class OrderMapper {

    @Autowired
    private CustomerRepository customerRepository;

    @Mapping(source = "customer.id", target = "customerId")
    public abstract OrderDTO toDTO(Order order);

    @Mapping(source = "customerId", target = "customer", qualifiedByName = "mapCustomerIdToCustomer")
    public abstract Order toEntity(OrderDTO orderDTO);

    @Named("mapCustomerIdToCustomer")
    protected Customer mapCustomerIdToCustomer(Integer customerId) {
        if (customerId == null) {
            return null;
        }
        // Use repository to get Customer entity by ID
        return customerRepository.findById(customerId).orElse(null);
    }
}