package com.example.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDTO {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productDescription;
    private String imageUrl;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal price;
}