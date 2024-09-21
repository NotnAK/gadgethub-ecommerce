package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;


    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = true)
    private Brand brand;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Integer popularity = 0;

    @Column
    private Boolean isActive = true;
}
