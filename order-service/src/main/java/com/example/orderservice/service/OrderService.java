package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import com.example.orderservice.webclient.client.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrderService {

    @Value("${inventory.service.url}")
    private String inventoryUrl;

    private final InventoryClient inventoryClient;

    public OrderService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    public Order placeOrder(Order order) {
        boolean available = inventoryClient.checkStock(order.productCode(), order.quantity());
        if (available) {
            return new Order(order.id(), order.productCode(), order.quantity(), "ORDER_PLACED");
        } else {
            return new Order(order.id(), order.productCode(), order.quantity(), "OUT_OF_STOCK");
        }

    }

    public Mono<Order> placeOrderReactive(Order order) {
        return inventoryClient
                .checkStockReactive(order.productCode(), order.quantity())
                .map(available -> available ?
                        new Order(order.id(), order.productCode(), order.quantity(), "ORDER_PLACED")
                        : new Order(order.id(), order.productCode(), order.quantity(), "OUT_OF_STOCK"));
    }

    public Flux<Order> placeOrdersReactive(List<Order> orders) {
        return Flux.fromIterable(orders).flatMap(this::placeOrderReactive)
                .onErrorContinue((ex, obj) -> System.out.println("Error occurred: " + ex.getMessage()));
    }

}
