package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductInfoDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ImageStorageException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductInfoMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.BrandRepository;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductInfoMapper productInfoMapper;
    @Value("${upload.path}") // Add the path to the download folder to application.properties
    private String uploadPath;

    @Autowired
    public ProductService(ProductRepository productRepository, BrandRepository brandRepository,
                          CategoryRepository categoryRepository, ProductMapper productMapper, ProductInfoMapper productInfoMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productInfoMapper = productInfoMapper;
    }

    /**
     * Retrieves product information by its ID.
     *
     * @param id The ID of the product.
     * @return A ProductInfoDTO containing information about the product.
     * @throws ResourceNotFoundException if the product with the given ID is not found.
     */
    public ProductInfoDTO getProductInfoById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return productInfoMapper.toDTO(product);
    }

    /**
     * Creates a new product.
     *
     * @param productDTO The ProductDTO object containing product details.
     * @return A ProductDTO object representing the created product.
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        return productMapper.toDTO(productRepository.save(product));
    }

    /**
     * Saves the product image to the file system.
     *
     * @param image The image file to save.
     * @return The URL of the saved image.
     * @throws RuntimeException if there is an error while saving the image.
     */
    public String saveProductImage(MultipartFile image) {
        try {
            Path copyLocation = Paths
                    .get(uploadPath + File.separator + image.getOriginalFilename());
            Files.copy(image.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + image.getOriginalFilename();
        } catch (Exception e) {
            throw new ImageStorageException("Could not store image file. Error: " + e.getMessage());
        }
    }

    /**
     * Increases the popularity of a product by 1.
     *
     * @param productId The ID of the product to increment popularity.
     * @return The updated Product object with incremented popularity.
     * @throws ResourceNotFoundException if the product with the given ID is not found.
     */
    @Transactional
    public Product incrementProductPopularity(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        product.setPopularity(product.getPopularity() + 1);
        return productRepository.save(product);
    }

    /**
     * Updates an existing product in the system.
     *
     * @param productDTO The ProductDTO object containing updated product details.
     * @return A ProductDTO object representing the updated product.
     * @throws ResourceNotFoundException if the product with the given ID is not found.
     */
    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product product = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productDTO.getId()));
        productDTO.setImageUrl(product.getImageUrl());
        product = productMapper.toEntity(productDTO);
        return productMapper.toDTO(productRepository.save(product));
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @throws ResourceNotFoundException if the product with the given ID is not found.
     */
    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        productRepository.deleteById(id);
    }

    /**
     * Retrieves all active products with pagination.
     *
     * @param pageable The pagination information.
     * @return A page of ProductInfoDTO objects representing active products.
     */
    public Page<ProductInfoDTO> getAllActiveProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAllByIsActiveTrue(pageable);
        return productPage.map(productInfoMapper::toDTO);
    }

    /**
     * Retrieves all products with pagination.
     *
     * @param pageable The pagination information.
     * @return A page of ProductInfoDTO objects representing products.
     */
    public Page<ProductInfoDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productInfoMapper::toDTO);
    }
/*
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }*/


    /**
     * Retrieves all products ordered by popularity with pagination.
     *
     * @param pageable The pagination information.
     * @return A list of ProductDTO objects representing the most popular products.
     */
    public List<ProductDTO> getProductsByPopularity(Pageable pageable) {
        List<Product> products = productRepository.findAllByIsActiveTrueOrderByPopularityDesc(pageable);
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves products by category and brand with pagination.
     *
     * @param categoryId The ID of the category.
     * @param brandId    The ID of the brand.
     * @param pageable   The pagination information.
     * @return A page of ProductInfoDTO objects representing filtered products.
     */
    public Page<ProductInfoDTO> getProductsByCategoryAndBrand(Integer categoryId, Integer brandId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryIdAndBrandIdAndIsActiveTrue(categoryId, brandId, pageable);
        return productPage.map(productInfoMapper::toDTO);
    }

    /*    public List<ProductDTO> searchProducts(String query) {
        List<Product> products = productRepository.searchByQuery(query);
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }*/

    /**
     * Searches for products based on a query string with pagination.
     *
     * @param query    The search query.
     * @param pageable The pagination information.
     * @return A page of ProductInfoDTO objects representing search results.
     */
    public Page<ProductInfoDTO> searchProducts(String query, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByQuery(query, pageable);
        return productPage.map(productInfoMapper::toDTO);
    }

    /*    public List<ProductDTO> getProductsByCategory(Integer categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }*/

    /**
     * Retrieves products by category with pagination.
     *
     * @param categoryId The ID of the category.
     * @param pageable The pagination information.
     * @return A page of ProductInfoDTO objects representing filtered products by category.
     */
    public Page<ProductInfoDTO> getProductsByCategory(Integer categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        return productPage.map(productInfoMapper::toDTO);
    }


    /*
    public List<ProductDTO> getProductsByBrand(Integer brandId) {
        List<Product> products = productRepository.findByBrandId(brandId);
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }*/
    /**
     * Retrieves products by brand with pagination.
     *
     * @param brandId The ID of the brand.
     * @param pageable The pagination information.
     * @return A page of ProductInfoDTO objects representing filtered products by brand.
     */
    public Page<ProductInfoDTO> getProductsByBrand(Integer brandId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByBrandIdAndIsActiveTrue(brandId, pageable);
        return productPage.map(productInfoMapper::toDTO);
    }

    /*private void validateProductDTO(ProductDTO productDTO){
        if(productDTO.getBrandId() == null || !brandRepository.existsById(productDTO.getBrandId())){
            throw new IllegalArgumentException("Incorrect or missing brand for the product");
        }
        if(productDTO.getCategoryId() == null || !categoryRepository.existsById(productDTO.getCategoryId())){
            throw new IllegalArgumentException("Incorrect or missing category for the product");
        }
    }*/
}
