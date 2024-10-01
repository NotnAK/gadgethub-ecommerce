package com.example.ecommerce;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductInfoDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductInfoMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductInfoMapper productInfoMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;
    private ProductInfoDTO productInfoDTO;

    @BeforeEach
     public void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setPopularity(0);
        product.setIsActive(true);

        productDTO = new ProductDTO();
        productDTO.setId(1);
        productDTO.setName("Test Product");

        productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setId(1);
        productInfoDTO.setName("Test Product");
    }

    @Test
     void testGetProductInfoByIdSuccess() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productInfoMapper.toDTO(product)).thenReturn(productInfoDTO);

        ProductInfoDTO result = productService.getProductInfoById(1);

        assertEquals(1, result.getId());
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
     void testGetProductInfoByIdThrowsException() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductInfoById(1));
        verify(productRepository, times(1)).findById(1);
    }

    @Test
     void testCreateProduct() {
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(productDTO);

        assertEquals(1, result.getId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
     void testIncrementProductPopularitySuccess() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.incrementProductPopularity(1);

        assertEquals(1, result.getPopularity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
     void testIncrementProductPopularityThrowsException() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.incrementProductPopularity(1));
    }

    @Test
     void testUpdateProductSuccess() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.updateProduct(productDTO);

        assertEquals(1, result.getId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
     void testUpdateProductThrowsException() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productDTO));
    }

    @Test
     void testDeleteProductSuccess() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        productService.deleteProduct(1);

        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
     void testDeleteProductThrowsException() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1));
    }

    @Test
     void testGetAllActiveProducts() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAllByIsActiveTrue(any(Pageable.class))).thenReturn(productPage);
        when(productInfoMapper.toDTO(product)).thenReturn(productInfoDTO);

        Page<ProductInfoDTO> result = productService.getAllActiveProducts(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAllByIsActiveTrue(any(Pageable.class));
    }

    @Test
     void testGetAllProducts() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productInfoMapper.toDTO(product)).thenReturn(productInfoDTO);

        Page<ProductInfoDTO> result = productService.getAllProducts(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
     void testGetProductsByPopularity() {
        when(productRepository.findAllByIsActiveTrueOrderByPopularityDesc(any(Pageable.class)))
                .thenReturn(Collections.singletonList(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        List<ProductDTO> result = productService.getProductsByPopularity(Pageable.unpaged());

        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAllByIsActiveTrueOrderByPopularityDesc(any(Pageable.class));
    }
}
