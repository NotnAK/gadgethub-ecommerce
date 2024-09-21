package com.example.ecommerce.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Integer id;
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(max = 300, message = "Description must be less than 300 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be less than 0")
    private Integer quantity;


    private Integer categoryId;
    private Integer brandId;


    private String imageUrl;
    @Min(value = 0, message = "Popularity cannot be less than 0")
    private Integer popularity = 0;

    private Boolean isActive = true;
}
