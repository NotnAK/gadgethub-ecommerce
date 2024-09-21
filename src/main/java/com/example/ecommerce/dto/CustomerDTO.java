package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerDTO {
    private Integer id;

    @Email(message = "Please provide a valid email address. Example: example@gmail.com")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;


    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Full name cannot contain numbers or special characters")
    @NotBlank(message = "Full name is required")
    private String fullName;

    private Role role;
    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Please enter a valid phone number")
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
