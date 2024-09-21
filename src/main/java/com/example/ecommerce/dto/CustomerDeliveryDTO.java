package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CustomerDeliveryDTO {
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Full name cannot contain numbers or special characters")
    @NotBlank(message = "Full name is required")
    private String deliveryFullName;
    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Please enter a valid phone number")
    private String deliveryPhoneNumber;
}