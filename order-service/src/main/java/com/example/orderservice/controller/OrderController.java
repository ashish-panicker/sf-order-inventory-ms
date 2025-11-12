package com.example.orderservice.controller;

import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        return service.placeOrder(order);
    }
}
