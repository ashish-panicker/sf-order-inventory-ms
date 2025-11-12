package com.example.orderservice.model;

public record Order(Long id, String productCode, int quantity, String status) {}
