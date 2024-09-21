package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")

public class AdminRestController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final CartService cartService;
    private static final int PAGE_SIZE_USERS = 10;
    private static final int PAGE_SIZE_PRODUCTS = 6;
    private static final int PAGE_SIZE_ORDERS = 4;

    @Autowired
    public AdminRestController(ProductService productService, CategoryService categoryService,
                               BrandService brandService, CustomerService customerService,
                               OrderService orderService, CartService cartService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.cartService = cartService;

    }

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * Creates a new customer with validation and encodes the password.
     *
     * @param customerDTO CustomerDTO containing customer details
     * @param result      BindingResult containing validation results
     * @return ResponseEntity containing the created CustomerDTO
     */
    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @ModelAttribute CustomerDTO customerDTO, BindingResult result) {
        checkValidationErrors(result);
        customerDTO.setPassword(passwordEncoder.encode(customerDTO.getPassword()));
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }


    /**
     * Retrieves information about the current authenticated customer.
     *
     * @return ResponseEntity containing the CustomerInfoDTO of the current customer
     */
    @GetMapping("/info")
    public ResponseEntity<CustomerInfoDTO> getCurrentCustomerInfo() {
        Integer customerId = getCurrentUserId();  // Gets current user's ID
        CustomerInfoDTO customerInfoDTO = customerService.getCustomerInfoById(customerId);
        return ResponseEntity.ok(customerInfoDTO);
    }

    /**
     * Retrieves a paginated list of all customers.
     *
     * @param page Page number for pagination
     * @return ResponseEntity containing a page of CustomerDTOs
     */
    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerDTO>> viewCustomers(@RequestParam(required = false, defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE_USERS);
        Page<CustomerDTO> customerPage = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(customerPage);
    }


    /**
     * Retrieves a customer by their ID.
     *
     * @param id Customer ID
     * @return ResponseEntity containing the CustomerDTO of the specified customer
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Integer id) {
        CustomerDTO customerDTO = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerDTO);
    }


    /**
     * Updates a customer's information with validation.
     *
     * @param id             Customer ID
     * @param customerInfoDTO CustomerInfoDTO containing updated information
     * @param result         BindingResult containing validation results
     * @return ResponseEntity containing the updated CustomerDTO
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Integer id, @Valid @ModelAttribute CustomerInfoDTO customerInfoDTO, BindingResult result) {
        checkValidationErrors(result);
        CustomerDTO updatedUser = customerService.updateCustomer(id, customerInfoDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a customer by their ID, with a check to prevent self-deletion.
     *
     * @param id Customer ID
     * @return ResponseEntity with a success message if the customer is deleted
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> deleteCustomerById(@PathVariable Integer id) {
        if (id.equals(getCurrentUserId())) {
            throw new BadRequestException("You can't delete yourself");
        }
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    //----------------------------------------------------------------------------------------
    //Products
    /**
     * Retrieves a paginated list of all products.
     *
     * @param page Page number for pagination
     * @return ResponseEntity containing a page of ProductInfoDTOs
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductInfoDTO>> viewProducts(@RequestParam(required = false, defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE_PRODUCTS);
        Page<ProductInfoDTO> productPage = productService.getAllProducts(pageable);
        return ResponseEntity.ok(productPage);
    }

    /**
     * Creates a new product with image upload and validation.
     *
     * @param productDTO ProductDTO containing product details
     * @param image      MultipartFile containing the product image
     * @param result     BindingResult containing validation results
     * @return ResponseEntity containing the created ProductDTO
     */
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@Valid @ModelAttribute ProductDTO productDTO,
                                                    @RequestParam("image") MultipartFile image, BindingResult result) {
        checkValidationErrors(result); // Reusing validation check
        String imageUrl = productService.saveProductImage(image);  // Saves product image
        productDTO.setImageUrl(imageUrl);
        ProductDTO createdProduct = productService.createProduct(productDTO);  // Saves product
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }


    /**
     * Retrieves product details by ID.
     *
     * @param id Product ID
     * @return ResponseEntity containing the ProductInfoDTO of the specified product
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductInfoDTO> getProductById(@PathVariable Integer id) {
        ProductInfoDTO productDTO = productService.getProductInfoById(id);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * Updates product information with validation.
     *
     * @param id         Product ID
     * @param productDTO ProductDTO containing updated product details
     * @param result     BindingResult containing validation results
     * @return ResponseEntity containing the updated ProductDTO
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer id, @Valid @ModelAttribute ProductDTO productDTO, BindingResult result) {
        checkValidationErrors(result); // Reusing validation check
        productDTO.setId(id);  // Sets product ID for update
        ProductDTO updatedProduct = productService.updateProduct(productDTO);
        return ResponseEntity.ok(updatedProduct);
    }


    /**
     * Deletes a product by ID.
     *
     * @param id Product ID
     * @return ResponseEntity with a success message if the product is deleted
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product has been deactivated successfully.");
    }


//----------------------------------------------------------------------------------------
    //Categories

    /**
     * Retrieves all categories.
     *
     * @return ResponseEntity containing a list of CategoryDTOs
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> viewCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }


    /**
     * Retrieves a category by ID.
     *
     * @param id Category ID
     * @return ResponseEntity containing the CategoryDTO of the specified category
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Integer id) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    /**
     * Creates a new category with validation.
     *
     * @param categoryDTO CategoryDTO containing category details
     * @param result      BindingResult containing validation results
     * @return ResponseEntity containing the created CategoryDTO
     */
    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @ModelAttribute CategoryDTO categoryDTO, BindingResult result) {
        checkValidationErrors(result);
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Updates a category by ID with validation.
     *
     * @param id          Category ID
     * @param categoryDTO CategoryDTO containing updated category details
     * @param result      BindingResult containing validation results
     * @return ResponseEntity containing the updated CategoryDTO
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Integer id, @Valid @ModelAttribute CategoryDTO categoryDTO, BindingResult result) {
        checkValidationErrors(result);
        categoryDTO.setId(id);
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Deletes a category by ID.
     *
     * @param id Category ID
     * @return ResponseEntity with a success message if the category is deleted
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully!");
    }

//-------------------------------------------------------------------------------------
    //Brands

    /**
     * Retrieves all brands.
     *
     * @return ResponseEntity containing a list of BrandDTOs
     */
    @GetMapping("/brands")
    public ResponseEntity<List<BrandDTO>> viewBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    /**
     * Retrieves a brand by ID.
     *
     * @param id Brand ID
     * @return ResponseEntity containing the BrandDTO of the specified brand
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Integer id) {
        BrandDTO brandDTO = brandService.getBrandById(id);
        return ResponseEntity.ok(brandDTO);
    }


    /**
     * Creates a new brand with validation.
     *
     * @param brandDTO BrandDTO containing brand details
     * @param result   BindingResult containing validation results
     * @return ResponseEntity containing the created BrandDTO
     */
    @PostMapping("/brands")
    public ResponseEntity<BrandDTO> createBrand(@Valid @ModelAttribute BrandDTO brandDTO, BindingResult result) {
        checkValidationErrors(result);
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        return new ResponseEntity<>(createdBrand, HttpStatus.CREATED);
    }


    /**
     * Updates a brand by ID with validation.
     *
     * @param id       Brand ID
     * @param brandDTO BrandDTO containing updated brand details
     * @param result   BindingResult containing validation results
     * @return ResponseEntity containing the updated BrandDTO
     */
    @PutMapping("/brands/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Integer id, @Valid @ModelAttribute BrandDTO brandDTO, BindingResult result) {
        checkValidationErrors(result);
        brandDTO.setId(id);
        BrandDTO updatedBrand = brandService.updateBrand(brandDTO);
        return ResponseEntity.ok(updatedBrand);
    }


    /**
     * Deletes a brand by ID.
     *
     * @param id Brand ID
     * @return ResponseEntity with a success message if the brand is deleted
     */
    @DeleteMapping("/brands/{id}")
    public ResponseEntity<String> deleteBrandById(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted successfully!");
    }


    //----------------------------------------------------------------------------------
    //Order


    /**
     * Retrieves a paginated list of all orders.
     *
     * @param page Page number for pagination
     * @return ResponseEntity containing a page of OrderInfoDTOs
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderInfoDTO>> viewOrders(@RequestParam(required = false, defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE_ORDERS);
        Page<OrderInfoDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves a list of available order statuses.
     *
     * @return ResponseEntity containing a list of OrderStatus
     */
    @GetMapping("/order-statuses")
    public ResponseEntity<List<OrderStatus>> getOrderStatuses() {
        List<OrderStatus> statuses = List.of(OrderStatus.values());  // Получаем все статусы
        return ResponseEntity.ok(statuses);
    }


    /**
     * Retrieves all orders made by a specific customer.
     *
     * @param customerId Customer ID
     * @return ResponseEntity containing a list of OrderInfoDTOs
     */
    @GetMapping("/customer-orders")
    public ResponseEntity<List<OrderInfoDTO>> getOrdersByCustomerId(@RequestParam Integer customerId) {
        List<OrderInfoDTO> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id Order ID
     * @return ResponseEntity containing the OrderInfoDTO of the specified order
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderInfoDTO> getOrderById(@PathVariable Integer id) {
        OrderInfoDTO orderDTO = orderService.getOrderInfoById(id);
        return ResponseEntity.ok(orderDTO);
    }


    /**
     * Updates the status of an order by ID.
     *
     * @param id          Order ID
     * @param orderStatus New order status
     * @return ResponseEntity with a success message if the order status is updated
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Integer id, @RequestParam String orderStatus) {
        OrderStatus orderStatus1 = OrderStatus.valueOf(orderStatus);
        orderService.updateOrderStatus(id, orderStatus1);
        return ResponseEntity.ok("Order status updated successfully!");
    }


    /**
     * Deletes an order by ID.
     *
     * @param id Order ID
     * @return ResponseEntity with a success message if the order is deleted
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully!");
    }

    /**
     * Deletes a specific order item by its ID.
     *
     * @param id Order item ID
     * @return ResponseEntity with a success message if the order item is deleted
     */
    @DeleteMapping("/order-items/{id}")
    public ResponseEntity<String> deleteOrderItemById(@PathVariable Integer id) {
        orderService.deleteOrderItem(id);
        return ResponseEntity.ok("Order item deleted successfully!");
    }

    /**
     * Retrieves all items in a specific order by order ID.
     *
     * @param orderId Order ID
     * @return ResponseEntity containing a list of OrderItemDTOs
     */
    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<List<OrderItemDTO>> getOrderItems(@PathVariable Integer orderId) {
        List<OrderItemDTO> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }


    //------------------------------------------------------------------------------------
    //Cart


    /**
     * Retrieves a cart by its ID.
     *
     * @param id Cart ID
     * @return ResponseEntity containing the CartDTO of the specified cart
     */
    @GetMapping("/carts/{id}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Integer id) {
        CartDTO cartDTO = cartService.getCartById(id);
        return ResponseEntity.ok(cartDTO);
    }


    /**
     * Deletes a cart by its ID.
     *
     * @param id Cart ID
     * @return ResponseEntity with a success message if the cart is deleted
     */
    @DeleteMapping("/carts/{id}")
    public ResponseEntity<String> deleteCartById(@PathVariable Integer id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok("Cart deleted successfully!");
    }

    /**
     * Deletes a cart item by its ID.
     *
     * @param id Cart item ID
     * @return ResponseEntity with a success message if the cart item is deleted
     */
    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<String> deleteCartItemById(@PathVariable Integer id) {
        cartService.deleteCartItem(id);
        return ResponseEntity.ok("Cart item deleted successfully!");
    }

    /**
     * Retrieves all items in a specific cart by cart ID.
     *
     * @param cartId Cart ID
     * @return ResponseEntity containing a list of CartItemDTOs
     */
    @GetMapping("/carts/{cartId}/items")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable Integer cartId) {
        List<CartItemDTO> cartItems = cartService.getCartItemsByCartId(cartId);
        return ResponseEntity.ok(cartItems);
    }

    /**
     * Helper method to retrieve the current authenticated user's ID.
     *
     * @return Integer ID of the current user
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return customerService.findByEmail(user.getUsername()).getId();
    }
}
