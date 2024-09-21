package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CartItemMapper;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       CustomerRepository customerRepository, CartMapper cartMapper,
                       CartItemMapper cartItemMapper, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.productRepository = productRepository;
    }
    /**
     * Retrieves a cart by its ID.
     *
     * @param id The ID of the cart to retrieve.
     * @return A CartDTO object containing cart details.
     * @throws ResourceNotFoundException if the cart with the given ID is not found.
     */
    public CartDTO getCartById(Integer id) {
        return cartRepository.findById(id)
                .map(cartMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + id));
    }

    /**
     * Creates a new cart.
     *
     * @param cartDTO The CartDTO object containing information about the cart to create.
     * @return A CartDTO object containing the created cart's information.
     */
    @Transactional
    public CartDTO createCart(CartDTO cartDTO) {
        validateCartDTO(cartDTO);
        if(cartDTO.getCartItems() == null){
            cartDTO.setCartItems(new ArrayList<>());
        }
        Cart cart = cartMapper.toEntity(cartDTO);
        cart.setTotalPrice(BigDecimal.ZERO);
        return cartMapper.toDTO(cartRepository.save(cart));
    }

    /**
     * Retrieves the cart ID associated with a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return The ID of the customer's cart.
     * @throws ResourceNotFoundException if the customer does not have a cart.
     */
    public Integer getCartIdByCustomerId(Integer customerId) {
        return cartRepository.findByCustomerId(customerId)
                .map(Cart::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer with ID: " + customerId));
    }


    /**
     * Updates an existing cart.
     *
     * @param cartDTO The CartDTO object containing updated cart information.
     * @return A CartDTO object containing the updated cart's information.
     * @throws ResourceNotFoundException if the cart with the given ID is not found.
     */
    @Transactional
    public CartDTO updateCart(CartDTO cartDTO) {
        if (!cartRepository.existsById(cartDTO.getId())) {
            throw new ResourceNotFoundException("Cart not found with ID: " + cartDTO.getId());
        }
        validateCartDTO(cartDTO);

        Cart cart = cartMapper.toEntity(cartDTO);
        return cartMapper.toDTO(cartRepository.save(cart));
    }

    /**
     * Deletes a cart by its ID.
     *
     * @param id The ID of the cart to delete.
     * @throws ResourceNotFoundException if the cart with the given ID is not found.
     */
    @Transactional
    public void deleteCart(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cart not found with ID: " + id);
        }
        cartRepository.deleteById(id);
    }


    /**
     * Validates the provided CartDTO object.
     *
     * This method ensures that the CartDTO contains a valid customer ID.
     * If the customer ID is null or the customer does not exist in the database,
     * an IllegalArgumentException is thrown.
     *
     * @param cartDTO The CartDTO object to validate.
     * @throws IllegalArgumentException if the customer ID is null or if the customer does not exist in the system.
     */
    private void validateCartDTO(CartDTO cartDTO) {
        if (cartDTO.getCustomerId() == null || !customerRepository.existsById(cartDTO.getCustomerId())) {
            throw new IllegalArgumentException("Incorrect or missing user for the cart");
        }
    }



    /**
     * Adds an item to a cart.
     *
     * @param cartId The ID of the cart to add the item to.
     * @param productId The ID of the product to add.
     * @return A CartItemDTO object containing the added item's information.
     * @throws ResourceNotFoundException if the cart or product is not found.
     * @throws IllegalArgumentException if the requested quantity exceeds available stock.
     */
    @Transactional
    public CartItemDTO addCartItem(Integer cartId, Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        if(!product.getIsActive()){
            throw new ResourceNotFoundException("Product not found or was deleted");
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));
        // Check if the product is already in the cart
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // If the product is already in the cart, increase the quantity by 1
            cartItem = existingCartItem.get();
            if(cartItem.getQuantity() + 1 > product.getQuantity()){
                throw new IllegalArgumentException("Requested quantity exceeds available stock for product: " + product.getName());
            }
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            // We recalculate the price. Namely, we add the price of one product
            cartItem.setPrice(cartItem.getPrice().add(product.getPrice()));
        } else {
            if(product.getQuantity() < 1){
                throw new IllegalArgumentException("Requested quantity exceeds available stock for product: " + product.getName());
            }
            // If the product is not in the cart, create a new cart item with quantity 1
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(1);
            cartItem.setPrice(product.getPrice());
            cart.getCartItems().add(cartItem);

        }
        // Update the total value of the cart. In both cases I added only one item to the cart.
        cart.setTotalPrice(cart.getTotalPrice().add(product.getPrice()));
        return cartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }
    /**
     * Updates the quantity of an item in the cart.
     *
     * @param cartItemId The ID of the cart item to update.
     * @param newQuantity The new quantity of the cart item.
     * @return A CartItemDTO object containing updated information about the cart item.
     * @throws ResourceNotFoundException if the cart item or product is not found.
     * @throws IllegalArgumentException if the requested quantity exceeds available stock.
     */
    @Transactional
    public CartItemDTO updateCartItemQuantity(Integer cartItemId, Integer newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with ID: " + cartItemId));
        Product product = productRepository.findById(cartItem.getProduct().getId()).get();
        if(!product.getIsActive()){
            throw new ResourceNotFoundException("Product not found or was deleted");
        }
        if (newQuantity > product.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock for product: " + product.getName());
        }
        Cart cart = cartItem.getCart();

        // Update the total price of the cart before changing the quantity
        cart.setTotalPrice(cart.getTotalPrice().subtract(cartItem.getPrice()));

        // Update quantity and recalculate price for CartItem
        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(newQuantity)));

        // Update the total price of the cart after changing the quantity
        cart.setTotalPrice(cart.getTotalPrice().add(cartItem.getPrice()));
        cartRepository.save(cart); // Сохранить обновления корзины
        return cartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }

    /**
     * Deletes a cart item by its ID.
     *
     * @param cartItemId The ID of the cart item to delete.
     * @throws ResourceNotFoundException if the cart item is not found.
     */
    @Transactional
    public void deleteCartItem(Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with ID: " + cartItemId));
        Cart cart = cartItem.getCart();
        cart.setTotalPrice(cart.getTotalPrice().subtract(cartItem.getPrice()));
        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);
        cartItemRepository.deleteById(cartItemId);

    }
    /**
     * Clears all items from a cart.
     *
     * @param cartId The ID of the cart to clear.
     * @throws ResourceNotFoundException if the cart with the given ID is not found.
     */
    @Transactional
    public void clearCart(Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(cartItems);

        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
    /**
     * Retrieves a list of all items in a cart by cart ID.
     *
     * @param cartId The ID of the cart.
     * @return A list of CartItemDTO objects containing information about all items in the cart.
     * @throws ResourceNotFoundException if the cart with the given ID is not found.
     */
    public List<CartItemDTO> getCartItemsByCartId(Integer cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new ResourceNotFoundException("Cart not found with ID: " + cartId);
        }
        return cartItemRepository.findByCartId(cartId)
                .stream()
                .map(cartItemMapper::toDTO)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves a specific cart item by its ID.
     *
     * @param cartItemId The ID of the cart item to retrieve.
     * @return A CartItemDTO object containing information about the cart item.
     * @throws ResourceNotFoundException if the cart item with the given ID is not found.
     */
    public CartItemDTO getCartItemById(Integer cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .map(cartItemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));
    }
}
