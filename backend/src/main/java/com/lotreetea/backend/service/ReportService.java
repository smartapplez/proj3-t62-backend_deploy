package com.lotreetea.backend.service;

import com.lotreetea.backend.model.XReport;
import com.lotreetea.backend.model.ZReport;
import com.lotreetea.backend.repo.ReportDAO;
import com.lotreetea.backend.model.SalesReportRow;
import com.lotreetea.backend.model.ProductUsageRow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ReportDAO reportDAO;

    /**
     * Generates Z Report for the given date
     * @param zDate: The date for which the Z report is generated it yyyy-MM-dd format
     * @return ZReport object containing the report data
     */
    public ZReport getZReport(String zDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(zDate, fmt);

        ZReport z = new ZReport();

        if (reportDAO.isDayFinished(date)){ 
            System.out.println("ZREPORT ERROR: DAY HAS BEEN CLOSED"); 
            z.setReportDate("ZREPORT ERROR: DAY HAS BEEN CLOSED");
            return z; 
        }
        
        BigDecimal gross=null, refunds=null, cost=null, net=null;
        gross = BigDecimal.ZERO;
        refunds = BigDecimal.ZERO;
        cost = BigDecimal.ZERO;
        net = BigDecimal.ZERO;
        for (int h=9; h<=17; h++) {
            String hourString;
            if(h / 10 == 0) {
                // Single digit hour (e.g., 9:00) --> "09:00"
                hourString = "0" + h + ":00";
            } else {
                // Double digit hour (e.g., 10:00, 11:00, etc.)
                hourString = "" + h + ":00";
            }
            XReport x = generateXReport(hourString, zDate);
            gross = gross.add(x.getGrossSales());
            refunds = refunds.add(x.getRefunds());
            cost = cost.add(x.getCost());
            net = net.add(x.getNetSales());
        }
        z.setReportDate(zDate);
        z.setGrossSales(gross);
        z.setRefunds(refunds);
        z.setCost(cost);
        z.setNetSales(net);
        reportDAO.ranZ(date);
        return z;
    }

    /**
     * Generates an X Report with detailed cost calculation.
     */
    public XReport generateXReport(String reportTime, String reportDate) {
        LocalDate date = LocalDate.parse(reportDate, DateTimeFormatter.ISO_DATE);
        // Enforce strict parsing: always expect HH:mm (e.g., "09:00", "15:00")
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(reportTime, timeFormatter);

        // Checking if the day is finished
        XReport report = new XReport();
        if (reportDAO.isDayFinished(date)){
            System.out.println("XREPORT ERROR: DAY HAS BEEN CLOSED"); 
            report.setReportDate("XREPORT ERROR: DAY HAS BEEN CLOSED");
            return report; 
        }

        int hour = time.getHour();
        if(hour < 9 || hour > 17){
            System.err.println("[Service] GenerateXReport Datetime not within work range");
            return new XReport();
        }

        report = reportDAO.getXReport(date, hour);

        // Cost calculation remains the same
        String costSql =
            "SELECT COALESCE(SUM(mic.quantity * ii.cost * oi.quantity),0) " +
            "FROM orders o " +
            "JOIN order_items oi ON o.order_id=oi.order_id " +
            "JOIN menu_item_components mic ON oi.menu_item_id=mic.menu_item_id " +
            "JOIN inventory_items ii ON mic.inventory_item_id=ii.inventory_item_id " +
            "WHERE DATE(o.order_date)=? AND EXTRACT(HOUR FROM o.order_time)=? AND o.is_refund=FALSE";

        BigDecimal cost = BigDecimal.ZERO;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(costSql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setInt(2, hour);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) cost = rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error computing cost for hour", e);
        }

        report.setCost(cost);
        report.setNetSales(report.getGrossSales().subtract(cost));
        return report;
    }

    public List<SalesReportRow> generateSalesReport(LocalDate startDate, LocalDate endDate) {
        return reportDAO.getSalesReport(startDate, endDate);
    }

    public BigDecimal generateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return reportDAO.getTotalRevenue(startDate, endDate);
    }

    public List<ProductUsageRow> generateProductUsageReport(LocalDate startDate, LocalDate endDate) {
        return reportDAO.getProductUsage(startDate, endDate);
    }
}
