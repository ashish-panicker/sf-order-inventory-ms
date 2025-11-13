package com.example.inventoryservice.service;

import com.example.inventoryservice.model.InventoryItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InventoryService {

    private final List<InventoryItem> stock = new ArrayList<>(Arrays.asList(
            new InventoryItem("BOOK-001", 10),
            new InventoryItem("BOOK-002", 0),
            new InventoryItem("BOOK-003", 5)
    ));

    public boolean checkAvailability(String productCode, int qty) {
        return stock.stream()
                .filter(item -> item.productCode().equals(productCode))
                .anyMatch(item -> item.availableQuantity() >= qty);
    }

    public boolean deductFromStock(String productCode, int qty) {
        return stock.stream()
                .filter(item -> item.productCode().equals(productCode))
                .filter(item -> item.availableQuantity() >= qty)
                .findFirst()
                .map(item -> {
                    stock.set(stock.indexOf(item),
                            new InventoryItem(item.productCode(),
                                    item.availableQuantity() - qty));
                    return true;
                })
                .orElse(false);
    }

    public List<InventoryItem> getStock() {
        return stock;
    }

}
