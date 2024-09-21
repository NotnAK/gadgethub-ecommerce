package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.dto.CustomerInfoDTO;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CustomerInfoMapper;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CartService cartService;
    private final CustomerInfoMapper customerInfoMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper,
                           CartService cartService, CustomerInfoMapper customerInfoMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.cartService = cartService;
        this.customerInfoMapper = customerInfoMapper;
    }
    /**
     * Retrieves a customer by their ID.
     *
     * @param id The ID of the customer to retrieve.
     * @return A CustomerDTO object containing customer information.
     * @throws ResourceNotFoundException if the customer with the given ID is not found.
     */
    public CustomerDTO getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return customerMapper.toDTO(customer);
    }
    /**
     * Retrieves customer information for display purposes by their ID.
     *
     * @param id The ID of the customer to retrieve.
     * @return A CustomerInfoDTO object containing information about the customer.
     * @throws ResourceNotFoundException if the customer with the given ID is not found.
     */
    public CustomerInfoDTO getCustomerInfoById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return customerInfoMapper.toDTO(customer);
    }
    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email to check.
     * @return true if the user exists, false otherwise.
     */
    public boolean userExists(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }
    /**
     * Finds a customer by their email.
     *
     * @param email The email of the customer to find.
     * @return A CustomerDTO object containing customer information.
     * @throws ResourceNotFoundException if the customer with the given email is not found.
     */
    public CustomerDTO findByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
        return customerMapper.toDTO(customer);
    }
    /**
     * Creates a new customer.
     *
     * @param customerDTO The CustomerDTO object containing information about the new customer.
     * @return A CustomerDTO object containing the created customer's information.
     * @throws DuplicateResourceException if a customer with the same email already exists.
     */
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {

        // Checking for an existing user with the same e-mail account
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email is already in use by another account");
        }
        // Create a new user
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCustomerId(savedCustomer.getId());
        if(customer.getRole().toString().equals("USER")){
            // Create a shopping cart for a new user using CartService
            cartService.createCart(cartDTO);
        }
        return customerMapper.toDTO(savedCustomer);

    }
    /**
     * Updates the information of an existing customer.
     *
     * @param id             The ID of the customer to update.
     * @param customerInfoDTO The CustomerInfoDTO object containing updated customer information.
     * @return A CustomerDTO object containing the updated customer's information.
     * @throws ResourceNotFoundException if the customer with the given ID is not found.
     * @throws DuplicateResourceException if a customer with the same email already exists.
     */
    @Transactional
    public CustomerDTO updateCustomer(Integer id, CustomerInfoDTO customerInfoDTO) {
        // Checking if the user exists
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        Optional<Customer> customerWithSameEmail = customerRepository.findByEmail(customerInfoDTO.getEmail());
        // If another user with the same email exists, throw an exception
        if (customerWithSameEmail.isPresent() && !customerWithSameEmail.get().getId().equals(id)) {
            throw new DuplicateResourceException("Email is already in use by another account");
        }
        customer.setFullName(customerInfoDTO.getFullName());
        customer.setAddress(customerInfoDTO.getAddress());
        customer.setPhoneNumber(customerInfoDTO.getPhoneNumber());
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(updatedCustomer);
    }
    /**
     * Deletes a customer by their ID.
     *
     * @param id The ID of the customer to delete.
     * @throws ResourceNotFoundException if the customer with the given ID is not found.
     */
    @Transactional
    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }
    /**
     * Retrieves a paginated list of all customers.
     *
     * @param pageable The pagination information.
     * @return A Page object containing a list of CustomerDTO objects.
     */
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toDTO);
    }




}
