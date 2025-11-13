package com.example.inventoryservice.controller;

import com.example.inventoryservice.model.InventoryItem;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/check")
    public boolean checkStock(@RequestParam String code, @RequestParam int qty) {
        return service.checkAvailability(code, qty);
    }

    @PostMapping("/confirm/{code}/{qty}")
    public boolean deductFromStock(@PathVariable String code, @PathVariable int qty) {
        return service.deductFromStock(code, qty);
    }

    @GetMapping
    public List<InventoryItem> getStock() {
        return service.getStock();
    }
}