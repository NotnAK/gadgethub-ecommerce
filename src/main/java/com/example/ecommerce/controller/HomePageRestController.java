package com.example.ecommerce.controller;

import com.example.ecommerce.dto.BrandDTO;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductInfoDTO;
import com.example.ecommerce.service.BrandService;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomePageRestController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    private static final int PAGE_SIZE = 6;
    @Autowired
    public HomePageRestController(ProductService productService, CategoryService categoryService, BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    /**
     * Retrieves a paginated and optionally filtered list of products.
     * Products can be filtered by category, brand, or searched by a query.
     *
     * @param categoryId    the ID of the category to filter by (optional)
     * @param brandId       the ID of the brand to filter by (optional)
     * @param page          the page number for pagination (optional, defaults to 0)
     * @param sortField     the field to sort by (optional, defaults to "id")
     * @param sortDirection the sort direction (optional, defaults to "ASC")
     * @param query         the search query (optional)
     * @return a paginated list of products based on filters or search query
     */
    @GetMapping("/getAllProducts")
    public ResponseEntity<Page<ProductInfoDTO>> getProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String query) {

        // Set up sorting based on the request parameters
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
        Page<ProductInfoDTO> productPage;
        // Perform search or filtering based on the parameters
        if (query != null && !query.isEmpty() && categoryId == null && brandId == null) {
            productPage = productService.searchProducts(query, pageable);
        } else if (categoryId != null && brandId != null && categoryId != 0 && brandId != 0) {
            // Filter by both category and brand
            productPage = productService.getProductsByCategoryAndBrand(categoryId, brandId, pageable);
        } else if (categoryId != null && categoryId != 0) {
            // Filter by category only
            productPage = productService.getProductsByCategory(categoryId, pageable);
        } else if (brandId != null && brandId != 0) {
            // Filter by brand only
            productPage = productService.getProductsByBrand(brandId, pageable);
        } else {
            // No filters, return all active products
            productPage = productService.getAllActiveProducts(pageable);
        }

        return ResponseEntity.ok(productPage);
    }


    /**
     * Retrieves a list of popular products for the home page with pagination.
     *
     * @param page The page number for pagination
     * @return A list of popular products
     */
    @GetMapping("/get")
    public ResponseEntity<List<ProductDTO>> getHomePageProducts(@RequestParam(required = false, defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        List<ProductDTO> products = productService.getProductsByPopularity(pageable);
        return ResponseEntity.ok(products);
    }
    /**
     * Retrieves the detailed information for a product by its ID.
     *
     * @param id The ID of the product to retrieve
     * @return ProductInfoDTO with detailed product information
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductInfoDTO> getProductById(@PathVariable Integer id) {
        ProductInfoDTO productDTO = productService.getProductInfoById(id);
        return ResponseEntity.ok(productDTO);
    }
    /**
     * Retrieves a list of all available brands.
     *
     * @return A list of BrandDTO objects
     */
    @GetMapping("/brands")
    public ResponseEntity<List<BrandDTO>> getBrands(){
        List<BrandDTO> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }
    /**
     * Retrieves a list of all available categories.
     *
     * @return A list of CategoryDTO objects
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories(){
        List<CategoryDTO> categories =categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    /**
     * Retrieves a category by its ID.
     *
     * @param id The ID of the category to retrieve
     * @return CategoryDTO with detailed category information
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Integer id) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }
    /**
     * Retrieves a brand by its ID.
     *
     * @param id The ID of the brand to retrieve
     * @return BrandDTO with detailed brand information
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Integer id) {
        BrandDTO brandDTO = brandService.getBrandById(id);
        return ResponseEntity.ok(brandDTO);
    }
    /**
     * Searches for products based on a query and paginates the results.
     *
     * @param query The search query
     * @param page  The page number for pagination
     * @return A paginated list of products matching the search query
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String query,
                                                              @RequestParam(required = false, defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.Direction.DESC, "id");
        Page<ProductInfoDTO> productPage = productService.searchProducts(query, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent());
        response.put("currentPage", page);
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

}
