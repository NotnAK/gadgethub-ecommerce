package com.example.ecommerce.entity;

public enum OrderStatus {
    NEW,           // New order
    PROCESSING,    // Order in process
    SHIPPED,       // Order shipped
    DELIVERED,     // Order delivered
    CANCELLED,     // The order has been canceled
    RETURNED,      // Order returned
    COMPLETED      // Order completed
}