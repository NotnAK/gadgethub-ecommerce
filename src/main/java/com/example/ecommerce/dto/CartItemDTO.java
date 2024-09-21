package com.example.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class CartItemDTO {
    private Integer id;
    private Integer cartId;
    private ProductDTO product;
    private Integer quantity;
    private BigDecimal price;
}
