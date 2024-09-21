package com.example.ecommerce;


import com.example.ecommerce.dto.BrandDTO;
import com.example.ecommerce.entity.Brand;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.BrandMapper;
import com.example.ecommerce.repository.BrandRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private BrandService brandService;

    private Brand brand;
    private BrandDTO brandDTO;

    @BeforeEach
    public void setUp() {
        brand = new Brand();
        brand.setId(1);
        brand.setName("Test Brand");

        brandDTO = new BrandDTO();
        brandDTO.setId(1);
        brandDTO.setName("Test Brand");
    }

    @Test
    public void testGetBrandByIdSuccess() {
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));
        when(brandMapper.toDTO(brand)).thenReturn(brandDTO);

        BrandDTO result = brandService.getBrandById(1);

        assertEquals(1, result.getId());
        assertEquals("Test Brand", result.getName());
        verify(brandRepository, times(1)).findById(1);
        verify(brandMapper, times(1)).toDTO(brand);
    }

    @Test
    public void testGetBrandByIdThrowsException() {
        when(brandRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brandService.getBrandById(1));
        verify(brandRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateBrand() {
        when(brandMapper.toEntity(brandDTO)).thenReturn(brand);
        when(brandRepository.save(brand)).thenReturn(brand);
        when(brandMapper.toDTO(brand)).thenReturn(brandDTO);

        BrandDTO result = brandService.createBrand(brandDTO);

        assertEquals(brandDTO.getId(), result.getId());
        verify(brandRepository, times(1)).save(brand);
    }

    @Test
    public void testUpdateBrandSuccess() {
        when(brandRepository.existsById(1)).thenReturn(true);
        when(brandMapper.toEntity(brandDTO)).thenReturn(brand);
        when(brandRepository.save(brand)).thenReturn(brand);
        when(brandMapper.toDTO(brand)).thenReturn(brandDTO);

        BrandDTO result = brandService.updateBrand(brandDTO);

        assertEquals(brandDTO.getId(), result.getId());
        verify(brandRepository, times(1)).existsById(1);
        verify(brandRepository, times(1)).save(brand);
    }

    @Test
    public void testUpdateBrandThrowsException() {
        when(brandRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> brandService.updateBrand(brandDTO));
        verify(brandRepository, times(1)).existsById(1);
    }

    @Test
    public void testDeleteBrandSuccess() {
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));

        brandService.deleteBrand(1);

        verify(productRepository, times(1)).clearBrandFromProducts(1);
        verify(brandRepository, times(1)).delete(brand);
    }

    @Test
    public void testDeleteBrandThrowsException() {
        when(brandRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> brandService.deleteBrand(1));
        verify(brandRepository, times(1)).findById(1);
    }

    @Test
    public void testGetAllBrands() {
        List<Brand> brandList = new ArrayList<>();
        brandList.add(brand);

        when(brandRepository.findAll()).thenReturn(brandList);
        when(brandMapper.toDTO(brand)).thenReturn(brandDTO);

        List<BrandDTO> result = brandService.getAllBrands();

        assertEquals(1, result.size());
        verify(brandRepository, times(1)).findAll();
    }
}
