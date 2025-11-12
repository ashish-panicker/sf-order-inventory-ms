package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Value("${inventory.service.url}")
    private String inventoryUrl;

    private final RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Order placeOrder(Order order) {
        Boolean available = restTemplate.getForObject(
                inventoryUrl + "/check?code=" + order.productCode() + "&qty=" + order.quantity(),
                Boolean.class);

        if (Boolean.TRUE.equals(available)) {
            return new Order(order.id(), order.productCode(), order.quantity(), "ORDER_PLACED");
        } else {
            return new Order(order.id(), order.productCode(), order.quantity(), "OUT_OF_STOCK");
        }
    }

}
