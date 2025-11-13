package com.example.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "inventory-service",
        url = "${inventory.service.url}",
        fallback = InventoryClientFallback.class
)
public interface InventoryFeignClient {

    @GetMapping("/check")
    boolean checkStock(@RequestParam String code, @RequestParam int qty);
}
