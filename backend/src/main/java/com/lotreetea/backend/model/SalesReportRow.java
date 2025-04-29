package com.lotreetea.backend.model;

import java.math.BigDecimal;

public class SalesReportRow {
    private String itemName;
    private int totalQuantity;
    private BigDecimal totalRevenue;

    public SalesReportRow() {}

    public SalesReportRow(String itemName, int totalQuantity, BigDecimal totalRevenue) {
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    // Getters and setters
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}