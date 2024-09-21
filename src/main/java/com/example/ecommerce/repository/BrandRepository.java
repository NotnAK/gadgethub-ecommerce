package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByName(String brandName);
}
