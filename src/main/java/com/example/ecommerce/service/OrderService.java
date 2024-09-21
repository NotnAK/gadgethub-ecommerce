package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderInfoMapper;
import com.example.ecommerce.mapper.OrderItemMapper;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final OrderInfoMapper orderInfoMapperImpl;


    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, CustomerRepository customerRepository,
                        OrderMapper orderMapper, OrderItemMapper orderItemMapper, CartService cartService, CartRepository cartRepository, ProductService productService, OrderInfoMapper orderInfoMapperImpl) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.orderInfoMapperImpl = orderInfoMapperImpl;
    }
    /**
     * Retrieves all orders with pagination support.
     *
     * @param pageable Pagination information.
     * @return A page of OrderInfoDTO objects representing the orders.
     */
    public Page<OrderInfoDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderInfoMapperImpl::toDTO);
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return An OrderDTO object containing order details.
     * @throws ResourceNotFoundException if the order with the given ID is not found.
     */
    public OrderDTO getOrderById(Integer id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    /**
     * Retrieves detailed order information by order ID.
     *
     * @param id The ID of the order to retrieve.
     * @return An OrderInfoDTO object containing detailed information about the order.
     * @throws ResourceNotFoundException if the order with the given ID is not found.
     */
    public OrderInfoDTO getOrderInfoById(Integer id) {
        return orderRepository.findById(id)
                .map(orderInfoMapperImpl::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    /**
     * Creates an order from the cart.
     *
     * @param cartId      The ID of the cart from which to create the order.
     * @param deliveryDTO The delivery information for the order.
     * @return An OrderDTO object representing the created order.
     * @throws ResourceNotFoundException if the cart is not found or products in the cart are unavailable.
     */
    @Transactional
    public OrderDTO createOrderFromCart(Integer cartId, CustomerDeliveryDTO deliveryDTO) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));;
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order from an empty cart.");
        }
        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        List<OrderItem> orderItems = cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    if(cartItem.getProduct().getQuantity() < cartItem.getQuantity() || cartItem.getProduct().getQuantity() < 1){
                        throw new IllegalArgumentException("Product '" + cartItem.getProduct().getName() + "' is no longer available in the requested quantity.");

                    }
                    if(!cartItem.getProduct().getIsActive()){
                        throw new ResourceNotFoundException("Product '" + cartItem.getProduct().getName() + "' not found or was deleted");
                    }
                    OrderItem orderItem = new OrderItem();
                    // Increase the popularity of the product when added to an order
                    Product product = productService.incrementProductPopularity(cartItem.getProduct().getId());
                    product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                    orderItem.setProductId(product.getId());
                    orderItem.setProductName(product.getName());
                    orderItem.setProductDescription(product.getDescription());
                    orderItem.setProductPrice(product.getPrice());
                    orderItem.setImageUrl(product.getImageUrl());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);
        order.setStatus(OrderStatus.NEW);  // Set the initial status as NEW
        order.setTotalPrice(calculateTotalPrice(order.getOrderItems()));
        order.setDeliveryFullName(deliveryDTO.getDeliveryFullName());
        order.setDeliveryAddress(deliveryDTO.getDeliveryAddress());
        order.setDeliveryPhoneNumber(deliveryDTO.getDeliveryPhoneNumber());
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        cartService.clearCart(cartId);
        return orderMapper.toDTO(order);
    }

    /**
     * Calculates the total price of the order by summing up the price of each order item.
     *
     * @param orderItems A list of OrderItem objects that are part of the order.
     * @return The total price as a BigDecimal by summing the prices of each order item.
     */
    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    /**
     * Validates the OrderDTO object to ensure that it contains valid data.
     *
     * @param orderDTO The OrderDTO object to validate.
     * @throws IllegalArgumentException if the customer ID is null or the customer does not exist in the repository.
     */
    private void validateOrderDTO(OrderDTO orderDTO) {
        if (orderDTO.getCustomerId() == null || !customerRepository.existsById(orderDTO.getCustomerId())) {
            throw new IllegalArgumentException("Incorrect or missing user for the order");
        }
    }

    /**
     * Updates an existing order.
     *
     * @param orderDTO The OrderDTO object containing updated order information.
     * @return An OrderDTO object representing the updated order.
     * @throws ResourceNotFoundException if the order is not found.
     */
    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO) {
        if (!orderRepository.existsById(orderDTO.getId())) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderDTO.getId());
        }
        validateOrderDTO(orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDTO(order);
    }
    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     * @throws ResourceNotFoundException if the order with the given ID is not found.
     */
    @Transactional
    public void deleteOrder(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    /**
     * Deletes an order item by its ID.
     *
     * @param orderItemId The ID of the order item to delete.
     * @throws ResourceNotFoundException if the order item is not found.
     */
    @Transactional
    public void deleteOrderItem(Integer orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with ID: " + orderItemId));

        Order order = orderItem.getOrder();
        order.setTotalPrice(order.getTotalPrice().subtract(orderItem.getPrice()));
        order.getOrderItems().remove(orderItem);
        orderRepository.save(order);
        orderItemRepository.deleteById(orderItemId);
    }
    /**
     * Updates the status of an existing order.
     *
     * @param orderId The ID of the order to update.
     * @param status  The new status to set for the order.
     * @throws ResourceNotFoundException if the order is not found.
     */
    @Transactional
    public void updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
    }
    /**
     * Retrieves all order items by order ID.
     *
     * @param orderId The ID of the order to retrieve items for.
     * @return A list of OrderItemDTO objects representing the order items.
     * @throws ResourceNotFoundException if the order is not found.
     */
    public List<OrderItemDTO> getOrderItemsByOrderId(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves all orders for a specific customer by their ID.
     *
     * @param customerId The ID of the customer to retrieve orders for.
     * @return A list of OrderInfoDTO objects representing the orders.
     */
    public List<OrderInfoDTO> getOrdersByCustomerId(Integer customerId) {
        return orderRepository.findByCustomerIdOrderByIdDesc(customerId)
                .stream()
                .map(orderInfoMapperImpl::toDTO)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves all orders for a specific customer by their ID with pagination support.
     *
     * @param customerId The ID of the customer to retrieve orders for.
     * @param pageable   Pagination information.
     * @return A page of OrderDTO objects representing the orders.
     */
    public Page<OrderDTO> getOrdersByCustomerIdPageable(Integer customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(orderMapper::toDTO);
    }
}
