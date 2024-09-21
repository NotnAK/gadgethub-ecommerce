package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.category c " +
            "WHERE p.isActive = true " +
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    List<Product> searchByQuery(@Param("query") String query);


    @Query("SELECT p FROM Product p " +
            "JOIN p.brand b " +
            "JOIN p.category c " +
            "WHERE p.isActive = true " +
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    Page<Product> searchByQuery(@Param("query")String query, Pageable pageable);

    List<Product> findByBrandIdAndIsActiveTrue(Integer brandId);
    Page<Product> findByBrandIdAndIsActiveTrue(Integer brandId, Pageable pageable);
    List<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId);
    Page<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId, Pageable pageable);
    List<Product> findAllByIsActiveTrueOrderByPopularityDesc(Pageable pageable);

    Page<Product> findByCategoryIdAndBrandIdAndIsActiveTrue(Integer categoryId, Integer brandId, Pageable pageable);


    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.brand = null WHERE p.brand.id = :brandId")
    void clearBrandFromProducts(@Param("brandId") Integer brandId);

    @Modifying
    @Query("UPDATE Product p SET p.category = null WHERE p.category.id = :categoryId")
    void clearCategoryFromProducts(@Param("categoryId") Integer categoryId);
}
