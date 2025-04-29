package com.lotreetea.backend.model;

import java.math.BigDecimal;

public class ZReport {
    private String reportDate;
    private BigDecimal netSales;
    private BigDecimal grossSales;
    private BigDecimal refunds;
    private BigDecimal cost;

    public ZReport() {
        this.reportDate = "";
        this.netSales = BigDecimal.ZERO;
        this.grossSales = BigDecimal.ZERO;
        this.refunds = BigDecimal.ZERO;
        this.cost = BigDecimal.ZERO;
    }

    // Getters and setters
    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }

    public BigDecimal getNetSales() { return netSales; }
    public void setNetSales(BigDecimal netSales) { this.netSales = netSales; }

    public BigDecimal getGrossSales() { return grossSales; }
    public void setGrossSales(BigDecimal grossSales) { this.grossSales = grossSales; }

    public BigDecimal getRefunds() { return refunds; }
    public void setRefunds(BigDecimal refunds) { this.refunds = refunds; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
}