package com.example.defectapi.inventory;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService {
    private final Map<String, InventoryItem> inventory = new ConcurrentHashMap<>();

    public InventoryService() {
        inventory.put("BK-100", new InventoryItem("BK-100", "Spring in Action", 25));
        inventory.put("BK-200", new InventoryItem("BK-200", "Effective Java", 3));
    }

    public InventoryItem findBySku(String sku) {
        return inventory.get(sku);
    }

    public InventoryItem reserve(String sku, int requestedQuantity) {
        InventoryItem item = inventory.get(sku);
        if (item == null) {
            throw new IllegalArgumentException("Unknown SKU: " + sku);
        }

        // DEF-103: reservation boundary failure. The runtime values and required rule are log-only.
        if (requestedQuantity <= item.getAvailableQuantity()) {
            throw new InsufficientInventoryException(
                    "Insufficient inventory for " + sku + ": requested=" + requestedQuantity
                            + ", available=" + item.getAvailableQuantity());
        }

        item.reserve(requestedQuantity);
        return item;
    }
}
