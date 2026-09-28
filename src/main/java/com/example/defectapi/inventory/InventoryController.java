package com.example.defectapi.inventory;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{sku}")
    public InventoryItem findBySku(@PathVariable String sku) {
        return inventoryService.findBySku(sku);
    }

    @PutMapping("/{sku}/reserve")
    public InventoryItem reserve(@PathVariable String sku,
                                 @Valid @RequestBody ReserveInventoryRequest request) {
        return inventoryService.reserve(sku, request.getQuantity());
    }
}

