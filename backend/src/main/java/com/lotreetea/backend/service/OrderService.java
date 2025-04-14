package com.lotreetea.backend.service;

import com.lotreetea.backend.model.Order;
import com.lotreetea.backend.model.OrderItem;
import com.lotreetea.backend.repo.OrderRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@Slf4j //lombok auto logger
@Transactional(rollbackOn = Exception.class) // if exception undo 
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order getOrderById(Integer id) {
        return orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order createOrder(Order order) {
        // Set orderDate and orderTime to current values if not provided by the client
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        if (order.getOrderTime() == null) {
            order.setOrderTime(LocalTime.now());
        }
        if (order.getIsRefund() == null) {
            order.setIsRefund(false);
        }

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(order); // this links each item to the parent
            }
        }

        // Cascade saving: the orderItems will be automatically saved with the order
        return orderRepo.save(order);
    }

    // TODO: Updating does not work at the moment, need to check the orderItem relationship (orphanRemoveal)
    public Order updateOrder(Integer id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);
        existingOrder.setOrderDate(updatedOrder.getOrderDate());
        existingOrder.setOrderTime(updatedOrder.getOrderTime());
        existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
        existingOrder.setCashierId(updatedOrder.getCashierId());
        existingOrder.setIsRefund(updatedOrder.getIsRefund());

        // Update order items if provided
        if (updatedOrder.getOrderItems() != null) {
            for (OrderItem item : updatedOrder.getOrderItems()) {
                item.setOrder(existingOrder); // this links each item to the parent
            }
            existingOrder.setOrderItems(updatedOrder.getOrderItems()); // update the list of items
        }

        return orderRepo.save(existingOrder);

        /* ERROR:
         * [Request processing failed: org.springframework.orm.jpa.JpaSystemException: A collection with orphan deletion was no longer referenced by the owning entity instance: com.lotreetea.backend.model.Order.orderItems] with root cause
         * A collection with orphan deletion was no longer referenced by the owning entity instance: com.lotreetea.backend.model.Order.orderItems
         */
    }

    public void deleteOrder(Integer id) {
        Order order = getOrderById(id); // throws if not found
        orderRepo.delete(order);
    }
}
