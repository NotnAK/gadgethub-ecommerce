package com.example.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ProductInfoDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String categoryName;
    private String brandName;
    private String imageUrl;
    private Integer popularity = 0;
    private Boolean isActive;
}
