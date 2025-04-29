package com.lotreetea.backend.resource;

import com.lotreetea.backend.model.XReport;
import com.lotreetea.backend.model.ZReport;
import com.lotreetea.backend.repo.ReportDAO;
import com.lotreetea.backend.service.ReportService;
import com.lotreetea.backend.model.SalesReportRow;
import com.lotreetea.backend.model.ProductUsageRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportResource {

    @Autowired
    private ReportService reportService;

    @GetMapping("/z")
    public ResponseEntity<ZReport> getZReport(@RequestParam String date) {
        ZReport report = reportService.getZReport(date);
        if (report == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(report);
    }

    @GetMapping("/x")
    public ResponseEntity<XReport> getXReport(
            @RequestParam String date,
            @RequestParam String time) {
        XReport report = reportService.generateXReport(time, date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<SalesReportRow>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateSalesReport(startDate, endDate));
    }

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateTotalRevenue(startDate, endDate));
    }

    @GetMapping("/product-usage")
    public ResponseEntity<List<ProductUsageRow>> getProductUsageReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateProductUsageReport(startDate, endDate));
    }
}

/**
 * JSON String Documentation (NOTE: All of the following JSON requests are GET requests):
 * 
 **************************************** 
 * ZReport:
 * Parameters: date (yyyy-MM-dd)
 * NOTE: date MUST be in the format yyyy-MM-dd (e.g. 2023-10-01)
 * 
 * Example GET request: http://localhost:8081/reports/z?date=2025-04-21
 * GET RESPONSE FORMAT EXAMPLE:
 * {
 *     "reportDate": "2025-04-21",
 *     "netSales": 142.99999977,
 *     "grossSales": 238.00,
 *     "refunds": 0.00,
 *     "cost": 95.00000
 * }
 * 
 * Error Response Format if the date you requested is closed (i.e. Z report has been run):
 * 
 * {
        "reportDate": "ZREPORT ERROR: DAY HAS BEEN CLOSED",
        "reportTime": "",
        "netSales": 0.0,
        "grossSales": 0.0,
        "refunds": 0.0,
        "cost": 0.0
    }
 *************************************************** 
 * XReport:
 * Parameters: date (yyyy-MM-dd), time (hh:mm)
 * NOTE: time MUST be in the format hh:mm (e.g. 09:00)
 * 
 * Example GET request: http://localhost:8081/reports/x?date=2025-04-21&time=15:00
 * GET RESPONSE FORMAT EXAMPLE:
 * {
    "reportDate": "2025-04-21",
    "reportTime": "15:00",
    "netSales": 42.599999999999987,
    "grossSales": 66.00,
    "refunds": 0,
    "cost": 23.400000000000013
    }

    Example GET request: http://localhost:8081/reports/x?date=2025-04-21&time=09:00
    GET RESPONSE FORMAT EXAMPLE:
    {
        "reportDate": "2025-04-21",
        "reportTime": "9:00",
        "netSales": 0.0,
        "grossSales": 0.0,
        "refunds": 0.0,
        "cost": 0.0
    }
 NOTE: The reportTime can be h:mm (e.g. 9:00) or hh:mm (e.g. 10:00) when making the request.
    This is an issue from the ReportDAO that I am not going to fix heh

 * Error Response Format if the date you requested is closed (i.e. Z report has been run):
 * {
        "reportDate": "XREPORT ERROR: DAY HAS BEEN CLOSED",
        "reportTime": "",
        "netSales": 0.0,
        "grossSales": 0.0,
        "refunds": 0.0,
        "cost": 0.0
    }
 *************************************************** 
 * Sales Report:
 * Parameters: startDate (yyyy-MM-dd), endDate (yyyy-MM-dd)
 * 
 * Example GET request: http://localhost:8081/reports/sales?startDate=2025-03-12&endDate=2025-04-15
 * GET RESPONSE FORMAT EXAMPLE:
 * [
    {
        "itemName": "Ginger_Milk_Tea",
        "totalQuantity": 2,
        "totalRevenue": 10.00
    },
    {
        "itemName": "Matcha Milk Tea",
        "totalQuantity": 1,
        "totalRevenue": 5.50
    },
    {
        "itemName": "Taro_Milk_Tea",
        "totalQuantity": 1,
        "totalRevenue": 5.00
    }
   ]
 *************************************************** 
 * Revenue Report (NEW):
 * Parameters: startDate (yyyy-MM-dd), endDate (yyyy-MM-dd)
 * 
 * Example GET request: http://localhost:8081/reports/revenue?startDate=2025-03-12&endDate=2025-04-15
 * 
 * GET RESPONSE FORMAT EXAMPLE:
 * 20.50
 * 
 * NOTE: The revenue is the total revenue for the given date range.
 * 
 ****************************************************
 * Product Usage Report:
 * Parameters: startDate (yyyy-MM-dd), endDate (yyyy-MM-dd)
 * 
 * Example GET request: http://localhost:8081/reports/product-usage?startDate=2025-03-12&endDate=2025-04-15
 * 
 * GET RESPONSE FORMAT EXAMPLE:
 * [
    {
        "inventoryItemName": "Cup",
        "usedQuantity": 110
    },
    {
        "inventoryItemName": "Straw",
        "usedQuantity": 109
    },
    {
        "inventoryItemName": "Black_Tea",
        "usedQuantity": 73
    },
    {
        "inventoryItemName": "Milk",
        "usedQuantity": 65
    },
    {
        "inventoryItemName": "Ginger",
        "usedQuantity": 57
    },
    {
        "inventoryItemName": "Green_Tea",
        "usedQuantity": 23
    },
    {
        "inventoryItemName": "Passion Fruit",
        "usedQuantity": 8
    },
    {
        "inventoryItemName": "Lemonade",
        "usedQuantity": 7
    },
    {
        "inventoryItemName": "Strawberry",
        "usedQuantity": 7
    },
    {
        "inventoryItemName": "DavidRizz",
        "usedQuantity": 4
    },
    {
        "inventoryItemName": "Honey",
        "usedQuantity": 4
    },
    {
        "inventoryItemName": "Oolong_Tea",
        "usedQuantity": 3
    },
    {
        "inventoryItemName": "Taro",
        "usedQuantity": 3
    },
    {
        "inventoryItemName": "Coconut",
        "usedQuantity": 2
    },
    {
        "inventoryItemName": "Test",
        "usedQuantity": 2
    },
    {
        "inventoryItemName": "Passionfruit",
        "usedQuantity": 2
    },
    {
        "inventoryItemName": "Taro_Tea",
        "usedQuantity": 1
    },
    {
        "inventoryItemName": "Mango Lassi",
        "usedQuantity": 0
    },
    {
        "inventoryItemName": "Thai",
        "usedQuantity": 0
    },
    {
        "inventoryItemName": "Coffee",
        "usedQuantity": 0
    },
    {
        "inventoryItemName": "blue_tea",
        "usedQuantity": 0
    },
    {
        "inventoryItemName": "Tropical",
        "usedQuantity": 0
    },
    {
        "inventoryItemName": "test",
        "usedQuantity": 0
    },
    {
        "inventoryItemName": "Wintermelon",
        "usedQuantity": 0
    },
    {
        "inventoryItemName": "Lohit",
        "usedQuantity": 0
    }
  ]
 * 
 */