package com.example.ecommerce.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Integer id;
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 40, message = "Product name must be between 2 and 40 characters")
    private String name;
    @Size(max = 300, message = "Description must be less than 300 characters")
    private String description;
}
