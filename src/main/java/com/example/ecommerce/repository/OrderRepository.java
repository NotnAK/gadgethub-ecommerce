package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByCustomerId(Integer customerId, Pageable pageable);
    List<Order> findByCustomerIdOrderByIdDesc(Integer customerId);

}
