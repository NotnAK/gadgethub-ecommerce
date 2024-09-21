package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.exception.AuthenticationException;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.CustomerService;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {
    private final CartService cartService;
    private final OrderService orderService;
    private final CustomerService customerService;

    @Autowired
    public CustomerRestController(CartService cartService, OrderService orderService, CustomerService customerService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.customerService = customerService;
    }

    /**
     * Returns information about the currently authenticated customer.
     *
     * @return CustomerInfoDTO with customer details
     */
    @GetMapping("/info")
    public ResponseEntity<CustomerInfoDTO> getCurrentCustomerInfo() {
        Integer customerId = getCurrentCustomerId();
        CustomerInfoDTO customerInfoDTO = customerService.getCustomerInfoById(customerId);
        return ResponseEntity.ok(customerInfoDTO);
    }

    /**
     * Updates the current customer's information.
     *
     * @param customerInfoDTO The new customer information
     * @param result          Validation result
     * @return Updated CustomerDTO object
     */
    @PutMapping("/info")
    public ResponseEntity<CustomerDTO> updateCustomerInfo(@Valid @ModelAttribute CustomerInfoDTO customerInfoDTO, BindingResult result) {
        checkValidationErrors(result);
        Integer customerId = getCurrentCustomerId();
        CustomerDTO updatedCustomer = customerService.updateCustomer(customerId, customerInfoDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * Adds a product to the customer's cart.
     *
     * @param productId The ID of the product to add
     * @return Added CartItemDTO object
     */
    @PostMapping("/cart/items")
    public ResponseEntity<CartItemDTO> addCartItem(@RequestParam Integer productId) {
        Integer customerId = getCurrentCustomerId();
        Integer cartIdByCustomerId = cartService.getCartIdByCustomerId(customerId);
        CartItemDTO addedItem = cartService.addCartItem(cartIdByCustomerId, productId);
        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }


    /**
     * Retrieves all items in the current customer's cart.
     *
     * @return List of CartItemDTO objects
     */
    @GetMapping("/cart/items")
    public ResponseEntity<List<CartItemDTO>> getCartItems() {
        Integer customerId = getCurrentCustomerId();
        Integer cartIdByCustomerId = cartService.getCartIdByCustomerId(customerId);
        List<CartItemDTO> cartItems = cartService.getCartItemsByCartId(cartIdByCustomerId);
        return ResponseEntity.ok(cartItems);
    }


    /**
     * Deletes an item from the customer's cart.
     *
     * @param cartItemId The ID of the cart item to delete
     * @return Success message
     */
    @DeleteMapping("/carts/items/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Integer cartItemId) throws AccessDeniedException {
        Integer customerId = getCurrentCustomerId();
        Integer cartIdByCustomerId = cartService.getCartIdByCustomerId(customerId);
        CartItemDTO cartItem = cartService.getCartItemById(cartItemId);

        if (!cartItem.getCartId().equals(cartIdByCustomerId)) {
            throw new AccessDeniedException("You are not allowed to delete items from someone else's cart!");
        }
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok("Cart item deleted successfully!");
    }

    /**
     * Updates the quantity of an item in the customer's cart.
     *
     * @param cartItemId The ID of the cart item to update
     * @param newQuantity The new quantity
     * @return Updated CartItemDTO object
     */
    @PutMapping("/carts/items/{cartItemId}")
    public ResponseEntity<CartItemDTO> updateCartItemQuantity(@PathVariable Integer cartItemId, @RequestParam Integer newQuantity) throws AccessDeniedException {
        Integer customerId = getCurrentCustomerId();
        Integer cartIdByCustomerId = cartService.getCartIdByCustomerId(customerId);
        CartItemDTO cartItem = cartService.getCartItemById(cartItemId);

        // Ensure the item belongs to the customer's cart
        if (!cartItem.getCartId().equals(cartIdByCustomerId)) {
            throw new AccessDeniedException("You are not allowed to update items from someone else's cart!");
        }
        CartItemDTO updatedItem = cartService.updateCartItemQuantity(cartItemId, newQuantity);
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Clears all items from the customer's cart.
     *
     * @return Success message
     */
    @DeleteMapping("/cart/clear")
    public ResponseEntity<String> clearCart() {
        Integer customerId = getCurrentCustomerId();
        Integer cartIdByCustomerId = cartService.getCartIdByCustomerId(customerId);
        cartService.clearCart(cartIdByCustomerId);
        return ResponseEntity.ok("Cart cleared successfully!");
    }

    /**
     * Creates an order from the customer's cart.
     *
     * @param deliveryDTO Delivery details for the order
     * @param result Validation result
     * @return Created OrderDTO object
     */
    @PostMapping("/carts/order")
    public ResponseEntity<OrderDTO> createOrderFromCart(@Valid @ModelAttribute CustomerDeliveryDTO deliveryDTO, BindingResult result) {
        checkValidationErrors(result);
        Integer customerId = getCurrentCustomerId();
        Integer cartIdByCustomerId = cartService.getCartIdByCustomerId(customerId);
        OrderDTO createdOrder = orderService.createOrderFromCart(cartIdByCustomerId, deliveryDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * Retrieves all orders for the current customer.
     *
     * @return List of OrderInfoDTO objects
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderInfoDTO>> getOrdersByCustomerId() {
        Integer customerId = getCurrentCustomerId();
        List<OrderInfoDTO> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves a specific order by ID with a check to ensure it belongs to the current customer.
     *
     * @param orderId The ID of the order to retrieve
     *
     *
     * @return OrderDTO object if the order belongs to the current customer
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer orderId) throws AccessDeniedException {
        Integer customerId = getCurrentCustomerId();
        OrderDTO order = orderService.getOrderById(orderId);

        // Ensure the order belongs to the customer
        if (!order.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("You are not allowed to see this order");
        }

        return ResponseEntity.ok(order);
    }


    /**
     * Helper method to check for validation errors and throw a BadRequestException if any errors are found.
     *
     * @param result BindingResult containing validation results
     */
    private void checkValidationErrors(BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * Retrieves the ID of the currently authenticated customer.
     * @return The customer's ID
     */

    private Integer getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User is not authenticated.");
        }
        // If authenticating via OAuth2
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            String email = (String) oauthUser.getAttributes().get("email");
            return customerService.findByEmail(email).getId();
        }

        // If standard authentication (via a form)
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return customerService.findByEmail(user.getUsername()).getId();
        }

        throw new AuthenticationException("Authentication type not recognized or user is not authenticated.");

    }
}
