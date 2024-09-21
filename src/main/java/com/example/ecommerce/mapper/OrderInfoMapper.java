package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.OrderInfoDTO;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.repository.CustomerRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public abstract class OrderInfoMapper {

    @Autowired
    private CustomerRepository customerRepository;

    // Mapping from Order to OrderInfoDTO
    @Mapping(source = "customer.email", target = "customerEmail")
    @Mapping(source = "orderItems", target = "orderItems")
    public abstract OrderInfoDTO toDTO(Order order);

    // Mapping from OrderInfoDTO to Order (if required)
    @Mapping(source = "customerEmail", target = "customer", qualifiedByName = "mapCustomerEmailToCustomer")
    public abstract Order toEntity(OrderInfoDTO orderInfoDTO);

    // Mapping customer email to Customer object
    @Named("mapCustomerEmailToCustomer")
    protected Customer mapCustomerEmailToCustomer(String email) {
        if (email == null) {
            return null;
        }
        return customerRepository.findByEmail(email).orElse(null);
    }
}
