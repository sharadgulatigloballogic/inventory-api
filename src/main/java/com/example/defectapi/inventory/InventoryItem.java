package com.example.defectapi.inventory;

public class InventoryItem {
    private final String sku;
    private final String productName;
    private int availableQuantity;

    public InventoryItem(String sku, String productName, int availableQuantity) {
        this.sku = sku;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
    }

    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void reserve(int quantity) { availableQuantity -= quantity; }
}

