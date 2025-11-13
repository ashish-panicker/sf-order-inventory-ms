package com.example.orderservice.client;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class InventoryClientFallback implements InventoryFeignClient {

    private final Logger logger = Logger.getLogger(InventoryClientFallback.class.getName());

    @Override
    public boolean checkStock(String code, int qty) {
        logger.warning("Inventory service is not available");
        return false;
    }
}
