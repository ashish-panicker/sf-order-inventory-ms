package com.example.inventoryservice.service;

import com.example.inventoryservice.model.InventoryItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final List<InventoryItem> stock = List.of(
            new InventoryItem("BOOK-001", 10),
            new InventoryItem("BOOK-002", 0),
            new InventoryItem("BOOK-003", 5)
    );

    public boolean checkAvailability(String productCode, int qty) {
        return stock.stream()
                .filter(item -> item.productCode().equals(productCode))
                .anyMatch(item -> item.availableQuantity() >= qty);
    }
}
