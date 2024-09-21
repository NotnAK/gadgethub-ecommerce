package com.example.ecommerce;


import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CartItemMapper;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartItem cartItem;
    private CartDTO cartDTO;
    private CartItemDTO cartItemDTO;
    private Product product;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1);


        cart = new Cart();
        cart.setId(1);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setCartItems(new ArrayList<>());


        product = new Product();
        product.setId(1);
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(10);
        product.setIsActive(true);


        cartItem = new CartItem();
        cartItem.setId(1);
        cartItem.setQuantity(1);
        cartItem.setProduct(product);
        cartItem.setPrice(BigDecimal.valueOf(100));
        cartItem.setCart(cart);



        cartDTO = new CartDTO();
        cartDTO.setId(1);
        cartDTO.setCustomerId(1);
        cartDTO.setCartItems(new ArrayList<>());

        cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(1);
        cartItemDTO.setQuantity(1);


    }

    @Test
    public void testGetCartByIdSuccess() {
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.getCartById(1);

        assertEquals(1, result.getId());
        verify(cartRepository, times(1)).findById(1);
    }

    @Test
    public void testGetCartByIdThrowsException() {
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCartById(1));
        verify(cartRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateCart() {
        when(customerRepository.existsById(1)).thenReturn(true);
        when(cartMapper.toEntity(cartDTO)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartMapper.toDTO(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.createCart(cartDTO);

        assertEquals(1, result.getId());
        verify(cartRepository, times(1)).save(cart);
        verify(customerRepository, times(1)).existsById(1);
    }

    @Test
    public void testAddCartItemSuccess() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(cartItemMapper.toDTO(cartItem)).thenReturn(cartItemDTO);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemDTO result = cartService.addCartItem(1, 1);

        assertEquals(1, result.getId());
        verify(cartRepository, times(1)).findById(1);
        verify(productRepository, times(1)).findById(1);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    public void testAddCartItemThrowsExceptionIfProductNotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addCartItem(1, 1));
        verify(productRepository, times(1)).findById(1);
    }


    @Test
    public void testUpdateCartItemQuantityThrowsExceptionIfCartItemNotFound() {
        when(cartItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.updateCartItemQuantity(1, 2));
        verify(cartItemRepository, times(1)).findById(1);
    }

    @Test
    public void testDeleteCartItemSuccess() {
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(cartItem));

        cartService.deleteCartItem(1);

        verify(cartItemRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteCartItemThrowsException() {
        when(cartItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.deleteCartItem(1));
        verify(cartItemRepository, times(1)).findById(1);
    }

    @Test
    public void testClearCartSuccess() {
        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(1)).thenReturn(List.of(cartItem));

        cartService.clearCart(1);

        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        verify(cartItemRepository, times(1)).deleteAll(anyList());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void testGetCartItemsByCartIdSuccess() {
        when(cartRepository.existsById(1)).thenReturn(true);
        when(cartItemRepository.findByCartId(1)).thenReturn(List.of(cartItem));
        when(cartItemMapper.toDTO(cartItem)).thenReturn(cartItemDTO);

        List<CartItemDTO> result = cartService.getCartItemsByCartId(1);

        assertEquals(1, result.size());
        verify(cartRepository, times(1)).existsById(1);
        verify(cartItemRepository, times(1)).findByCartId(1);
    }

    @Test
    public void testGetCartItemsByCartIdThrowsException() {
        when(cartRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCartItemsByCartId(1));
        verify(cartRepository, times(1)).existsById(1);
    }
}
