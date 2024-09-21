package com.example.ecommerce;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.dto.CustomerInfoDTO;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CustomerInfoMapperImpl;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CartService cartService;

    @Mock
    private CustomerInfoMapperImpl customerInfoMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;
    private CustomerInfoDTO customerInfoDTO;
    private CartDTO cartDTO;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1);
        customer.setEmail("test@example.com");
        customer.setRole(Role.USER);


        customerDTO = new CustomerDTO();
        customerDTO.setId(1);
        customerDTO.setEmail("test@example.com");

        customerInfoDTO = new CustomerInfoDTO();
        customerInfoDTO.setFullName("Test User");
        customerInfoDTO.setEmail("test@example.com");

        cartDTO = new CartDTO();
        cartDTO.setCustomerId(1);
    }

    @Test
    public void testGetCustomerByIdSuccess() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.getCustomerById(1);

        assertEquals(1, result.getId());
        verify(customerRepository, times(1)).findById(1);
        verify(customerMapper, times(1)).toDTO(customer);
    }

    @Test
    public void testGetCustomerByIdThrowsException() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(1));
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateCustomerSuccess() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(customerMapper.toEntity(customerDTO)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertEquals(1, result.getId());
        verify(customerRepository, times(1)).save(customer);
        verify(cartService, times(1)).createCart(any(CartDTO.class));
    }

    @Test
    public void testCreateCustomerThrowsDuplicateException() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));

        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(customerDTO));
        verify(customerRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testUpdateCustomerSuccess() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.updateCustomer(1, customerInfoDTO);

        assertEquals(1, result.getId());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void testUpdateCustomerThrowsResourceNotFoundException() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(1, customerInfoDTO));
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    public void testDeleteCustomerSuccess() {
        when(customerRepository.existsById(1)).thenReturn(true);

        customerService.deleteCustomer(1);

        verify(customerRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteCustomerThrowsException() {
        when(customerRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer(1));
        verify(customerRepository, times(1)).existsById(1);
    }

    @Test
    public void testGetAllCustomersSuccess() {
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        Page<Customer> page = new PageImpl<>(customers);

        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

        Page<CustomerDTO> result = customerService.getAllCustomers(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(customerRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testFindByEmailSuccess() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.findByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
        verify(customerRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testFindByEmailThrowsException() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.findByEmail("test@example.com"));
        verify(customerRepository, times(1)).findByEmail("test@example.com");
    }
}
