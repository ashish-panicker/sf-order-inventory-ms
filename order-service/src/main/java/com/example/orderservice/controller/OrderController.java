package com.example.orderservice.controller;

import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @PostMapping("/reactive")
    public Mono<Order> placeOrderReactive(@RequestBody Order order) {
        return service.placeOrderReactive(order);
    }

    @PostMapping("/ractive-all")
    public Flux<Order> placeOrdersReactive(@RequestBody List<Order> orders) {
        return service.placeOrdersReactive(orders);
    }
}
