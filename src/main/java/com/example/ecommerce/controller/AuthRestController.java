package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRestController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Registers a new customer. Validates the provided CustomerDTO, encodes the password,
     * assigns the default role, and saves the customer in the system.
     *
     * @param customerDTO The customer data to register
     * @param result      The result of the validation check
     * @return ResponseEntity containing the created CustomerDTO and HTTP status code
     */
    @PostMapping("/register-customer")
    public ResponseEntity<CustomerDTO> registerUser(@Valid @ModelAttribute CustomerDTO customerDTO, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            throw new BadRequestException(errorMessage);
        }

        // Encrypt the user's password before saving to the database
        customerDTO.setPassword(passwordEncoder.encode(customerDTO.getPassword()));

        // Assign the default role to the user (Role.USER)
        customerDTO.setRole(Role.USER);

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

}
