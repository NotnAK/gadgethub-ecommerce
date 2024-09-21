package com.example.ecommerce.service;

import com.example.ecommerce.dto.BrandDTO;
import com.example.ecommerce.entity.Brand;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.BrandMapper;
import com.example.ecommerce.repository.BrandRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ProductRepository productRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.productRepository = productRepository;
    }
    /**
     * Retrieves a brand by its ID.
     *
     * @param id The ID of the brand to retrieve.
     * @return A BrandDTO object containing brand information.
     * @throws ResourceNotFoundException if the brand with the given ID is not found.
     */
    public BrandDTO getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));
        return brandMapper.toDTO(brand);
    }

    /**
     * Creates a new brand.
     *
     * @param brandDTO The BrandDTO object containing information about the brand to create.
     * @return A BrandDTO object containing the created brand's information.
     */
    @Transactional
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand brand = brandMapper.toEntity(brandDTO);
        return brandMapper.toDTO(brandRepository.save(brand));
    }
    /**
     * Updates an existing brand.
     *
     * @param brandDTO The BrandDTO object containing updated information about the brand.
     * @return A BrandDTO object containing the updated brand's information.
     * @throws ResourceNotFoundException if the brand with the given ID does not exist.
     */
    @Transactional
    public BrandDTO updateBrand(BrandDTO brandDTO) {
        if (!brandRepository.existsById(brandDTO.getId())) {
            throw new ResourceNotFoundException("Brand not found with ID: " + brandDTO.getId());
        }
        Brand brand = brandMapper.toEntity(brandDTO);
        return brandMapper.toDTO(brandRepository.save(brand));
    }

    /**
     * Deletes a brand by its ID.
     *
     * Removes the brand from associated products and then deletes the brand.
     *
     * @param id The ID of the brand to delete.
     * @throws ResourceNotFoundException if the brand with the given ID is not found.
     */
    @Transactional
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));
        productRepository.clearBrandFromProducts(id);
        brandRepository.delete(brand);
    }
    /**
     * Retrieves a list of all available brands.
     *
     * @return A list of BrandDTO objects containing information about all brands.
     */
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toDTO)
                .collect(Collectors.toList());
    }
}