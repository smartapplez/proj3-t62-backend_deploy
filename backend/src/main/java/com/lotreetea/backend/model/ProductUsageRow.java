package com.lotreetea.backend.model;

public class ProductUsageRow {
    private String inventoryItemName;
    private int usedQuantity;

    public ProductUsageRow() {}

    public ProductUsageRow(String inventoryItemName, int usedQuantity) {
        this.inventoryItemName = inventoryItemName;
        this.usedQuantity = usedQuantity;
    }

    // Getters and setters
    public String getInventoryItemName() { return inventoryItemName; }
    public void setInventoryItemName(String inventoryItemName) { this.inventoryItemName = inventoryItemName; }

    public int getUsedQuantity() { return usedQuantity; }
    public void setUsedQuantity(int usedQuantity) { this.usedQuantity = usedQuantity; }
}