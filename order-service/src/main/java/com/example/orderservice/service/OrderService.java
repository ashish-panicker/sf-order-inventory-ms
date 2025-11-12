package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Value("${inventory.service.url}")
    private String inventoryUrl;

    public Order placeOrder(Order order) {
        return null;
    }

}
