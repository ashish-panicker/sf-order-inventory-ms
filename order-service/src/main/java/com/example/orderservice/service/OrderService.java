package com.example.orderservice.service;

import com.example.orderservice.client.InventoryFeignClient;
import com.example.orderservice.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Value("${inventory.service.url}")
    private String inventoryUrl;

    private final InventoryFeignClient client;

    public OrderService(InventoryFeignClient client) {
        this.client = client;
    }

    @CircuitBreaker(name = "inventoryCB", fallbackMethod = "fallbackCreateOrder")
    public Order placeOrder(Order order) {
        boolean available = client.checkStock(order.productCode(), order.quantity());
        if (available) {
            return new Order(order.id(), order.productCode(), order.quantity(), "PLACED");
        }
        return null;
    }

    public Order fallbackCreateOrder(Order order, Throwable throwable) {
        return new Order(order.id(), order.productCode(), order.quantity(), "FAILED");
    }

}
