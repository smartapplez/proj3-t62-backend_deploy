package com.lotreetea.backend.repo;

import com.lotreetea.backend.model.XReport;
import com.lotreetea.backend.model.SalesReportRow;
import com.lotreetea.backend.model.ProductUsageRow;
import com.lotreetea.backend.model.ZReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ReportDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isDayFinished(LocalDate date) {
        String sql = "SELECT ranzreport FROM days WHERE date = ?";
        Boolean finished = jdbcTemplate.queryForObject(sql, new Object[]{date}, Boolean.class);
        return finished != null && finished;
    }

    public void ranZ(LocalDate date) {
        String sql = "UPDATE days SET ranzreport = TRUE WHERE date = ?";
        int rows = jdbcTemplate.update(sql, date);
        if (rows == 0) throw new RuntimeException("No row found for date: " + date);
    }

    public XReport getXReport(LocalDate date, int hour) {
        String grossSql =
            "SELECT COALESCE(SUM(total_amount),0) FROM orders " +
            "WHERE DATE(order_date)=? AND EXTRACT(HOUR FROM order_time)=? AND is_refund=FALSE";
        BigDecimal gross = jdbcTemplate.queryForObject(grossSql, new Object[]{date, hour}, BigDecimal.class);

        String refundSql =
            "SELECT COALESCE(SUM(total_amount),0) FROM orders " +
            "WHERE DATE(order_date)=? AND EXTRACT(HOUR FROM order_time)=? AND is_refund=TRUE";
        BigDecimal refunds = jdbcTemplate.queryForObject(refundSql, new Object[]{date, hour}, BigDecimal.class);

        XReport report = new XReport();
        report.setReportDate(date.toString());
        report.setReportTime(hour + ":00");
        report.setGrossSales(gross);
        report.setRefunds(refunds);
        report.setCost(BigDecimal.ZERO); // cost to be set by service
        report.setNetSales(BigDecimal.ZERO);
        return report;
    }

    public List<SalesReportRow> getSalesReport(LocalDate startDate, LocalDate endDate) {
        String sql =
            "SELECT mi.item_name AS item_name, SUM(oi.quantity) AS total_qty, SUM(oi.quantity * mi.price) AS total_revenue " +
            "FROM orders o " +
            "JOIN order_items oi ON o.order_id=oi.order_id " +
            "JOIN menu_items mi ON oi.menu_item_id=mi.menu_item_id " +
            "WHERE o.order_date BETWEEN ? AND ? AND o.is_refund=FALSE " +
            "GROUP BY mi.item_name ORDER BY total_revenue DESC";
        return jdbcTemplate.query(sql, new Object[]{startDate, endDate}, new RowMapper<SalesReportRow>() {
            @Override
            public SalesReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new SalesReportRow(
                    rs.getString("item_name"),
                    rs.getInt("total_qty"),
                    rs.getBigDecimal("total_revenue")
                );
            }
        });
    }

    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return getSalesReport(startDate, endDate).stream()
            .map(SalesReportRow::getTotalRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ProductUsageRow> getProductUsage(LocalDate startDate, LocalDate endDate) {
        String sql =
            "SELECT ii.item_name AS inventory_item, COALESCE(SUM(oi.quantity * mic.quantity),0) AS used_quantity " +
            "FROM inventory_items ii " +
            "LEFT JOIN menu_item_components mic ON ii.inventory_item_id=mic.inventory_item_id " +
            "LEFT JOIN menu_items mi ON mic.menu_item_id=mi.menu_item_id " +
            "LEFT JOIN order_items oi ON mi.menu_item_id=oi.menu_item_id " +
            "LEFT JOIN orders o ON oi.order_id=o.order_id AND o.order_date BETWEEN ? AND ? AND o.is_refund=FALSE " +
            "GROUP BY ii.item_name ORDER BY used_quantity DESC";
        return jdbcTemplate.query(sql, new Object[]{startDate, endDate}, new RowMapper<ProductUsageRow>() {
            @Override
            public ProductUsageRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ProductUsageRow(
                    rs.getString("inventory_item"),
                    rs.getInt("used_quantity")
                );
            }
        });
    }
}