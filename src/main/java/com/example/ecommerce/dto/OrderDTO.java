package com.example.ecommerce.dto;

import com.example.ecommerce.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Integer id;
    private Integer customerId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> orderItems;
    private String deliveryFullName;
    private String deliveryAddress;
    private String deliveryPhoneNumber;
}