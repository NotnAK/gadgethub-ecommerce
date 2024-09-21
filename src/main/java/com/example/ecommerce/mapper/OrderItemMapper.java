package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.repository.OrderRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public abstract class OrderItemMapper {

    @Autowired
    private OrderRepository orderRepository;

    // Mapping OrderItem entity to DTO
    @Mapping(source = "order.id", target = "orderId")
    public abstract OrderItemDTO toDTO(OrderItem orderItem);

    // Mapping a DTO to an OrderItem entity
    @Mapping(source = "orderId", target = "order", qualifiedByName = "mapOrderIdToOrder")
    public abstract OrderItem toEntity(OrderItemDTO orderItemDTO);

    // Method for mapping orderId to Order object
    @Named("mapOrderIdToOrder")
    protected Order mapOrderIdToOrder(Integer orderId) {
        if (orderId == null) {
            return null;
        }
        // Use repository to get Order entity by ID
        return orderRepository.findById(orderId).orElse(null);
    }
}