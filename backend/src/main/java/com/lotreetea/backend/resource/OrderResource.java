package com.lotreetea.backend.resource;

import com.lotreetea.backend.model.Order;
import com.lotreetea.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    // READ ALL
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // READ ONE (By ID)
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        // return ResponseEntity.created(URI.create("/orders/" + created.getOrderId())).body(order);
        return ResponseEntity.ok(created);
    }

    // UPDATE (PATCH)
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Integer id,
            @RequestBody Order updatedOrder) {
        Order updated = orderService.updateOrder(id, updatedOrder);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

/* JSON String Documentation:
 * GET Response Format (One Order):
 * {
        "orderId": 43,
        "orderDate": "2025-04-11",
        "orderTime": "01:44:25.343",
        "totalAmount": 9.50,
        "cashierId": 1,
        "isRefund": false,
        "orderItems": [
            {
                "orderItemId": 33,
                "sugarPercentage": 50,
                "icePercentage": 75,
                "quantity": 2,
                "menuItemId": 3,
                "isBoba": true,
                "isPopper": false,
                "isJelly": false
            },
            {
                "orderItemId": 34,
                "sugarPercentage": 100,
                "icePercentage": 25,
                "quantity": 1,
                "menuItemId": 6,
                "isBoba": false,
                "isPopper": true,
                "isJelly": true
            }
        ]
    }
 * 
 * POST/PUT Body/Reponse Format (One Order):
 * {
    "cashierId": 1,
    "totalAmount": 9.50,
    "orderItems": [
            {
                "menuItemId": 3,
                "sugarPercentage": 50,
                "icePercentage": 75,
                "quantity": 2,
                "isBoba": true,
                "isPopper": false,
                "isJelly": false
            },
            {
                "menuItemId": 6,
                "sugarPercentage": 100,
                "icePercentage": 25,
                "quantity": 1,
                "isBoba": false,
                "isPopper": true,
                "isJelly": true
            }
        ]
    }
 * 
 * NOTE: The orderDate and orderTime are automatically set to the current date and time when creating an order.
 *       The isRefund field defaults to false if not provided.
 * *       The orderItems list contains the details of each item in the order, including the menuItemId and customization options.
 *       The orderId is generated automatically and should not be included in the request body when creating a new order.
 * 
 * GETMapping (Order by ID):
 * GET http://localhost:8081/orders/{order_id}
 * 
 * GETMapping (All Orders):
 * GET http://localhost:8081/orders
 * 
 * PostMapping (Create Order):
 * POST http://localhost:8081/orders (with body shown above)
 * 
 * DeleteMapping (Delete Order by ID):
 * DELETE http://localhost:8081/orders/{order_id}
 * 
 * PUT Mapping is not supported
 * PATCH Mapping (Update Order by ID) is supported, but not reccommended for now. Make an Issue if you need it.
 * PATCH http://localhost:8081/orders/{order_id} (with body shown above)
 * 
 * @Paul, can you help me with the Paging for the getAllOrders() method? Love u pookie
 */