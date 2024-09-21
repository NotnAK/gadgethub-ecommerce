package com.example.ecommerce.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class CartDTO {
    private Integer id;
    private Integer customerId;
    private BigDecimal totalPrice;
    private List<CartItemDTO> cartItems;

}
