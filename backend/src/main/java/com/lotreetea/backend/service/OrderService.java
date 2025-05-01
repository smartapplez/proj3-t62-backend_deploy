package com.lotreetea.backend.service;

import com.lotreetea.backend.model.InventoryItem;
import com.lotreetea.backend.model.MenuItemComponent;
import com.lotreetea.backend.model.Order;
import com.lotreetea.backend.model.OrderItem;
import com.lotreetea.backend.repo.MenuItemComponentRepo;
import com.lotreetea.backend.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j // lombok auto logger
@Transactional(rollbackOn = Exception.class) // if exception undo
@RequiredArgsConstructor

public class OrderService {

  private final OrderRepo orderRepo;
  private final MenuItemComponentRepo menuItemComponentRepo;
  private final InventoryService inventoryService;

  public List<Order> getAllOrders() {
    return orderRepo.findAll();
  }

  public Order getOrderById(Integer id) {
    return orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
  }

  public Order createOrder(Order order) {
    if (order.getOrderDate() == null || order.getOrderDate() == "") {
      order.setOrderDate(LocalDate.now().toString());
    }
    if (order.getOrderTime() == null) {
      order.setOrderTime(LocalTime.now());
    }
    if (order.getIsRefund() == null) {
      order.setIsRefund(false);
    }

    if (order.getOrderItems() != null) {
      for (OrderItem item : order.getOrderItems()) {
        item.setOrder(order);
        List<MenuItemComponent> components = menuItemComponentRepo.findByMenuItem_MenuItemId(item.getMenuItemId());
        if (components.isEmpty()) {
          order.setOrderDate("ERROR: No components found for menu item id: " + item.getMenuItemId());
          return order;
          // throw new RuntimeException("No components found for menu item id: " + item.getMenuItemId());
        }
        for (MenuItemComponent comp : components) {
          double requiredQty = comp.getQuantity() * item.getQuantity();
          InventoryItem inventoryItem = inventoryService.getInventoryItem(comp.getInventoryItemId());
          if (inventoryItem.getStoredQuantity() < requiredQty) {
            order.setOrderDate("ERROR: Item '" + inventoryItem.getItemName()
                + "' is out of stock (needed: " + requiredQty + ", available: "
                + inventoryItem.getStoredQuantity() + "). Please remove this item from your order.");
          return order;
            // throw new RuntimeException("Item '" + inventoryItem.getItemName()
            //     + "' is out of stock (needed: " + requiredQty + ", available: "
            //     + inventoryItem.getStoredQuantity() + "). Please remove this item from your order.");
          }
          double newStoredQty = inventoryItem.getStoredQuantity() - requiredQty;
          inventoryItem.setStoredQuantity(newStoredQty);
          inventoryService.updateInventoryItem(inventoryItem.getInventoryItemId(), inventoryItem);
        }
      }
    }

    return orderRepo.save(order);
  }

  // NOTE: This method "restores" inventory first. However, it does not revert the
  // order upon "out of stock" exception and will break.
  // Let me know if it is necessary to use this method often.
  public Order updateOrder(Integer orderId, Order updatedOrder) {
    // Retrieve the existing order
    Order existingOrder = orderRepo.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));

    // FIRST: RESTORE INVENTORY for the existing order's items.
    if (existingOrder.getOrderItems() != null) {
      for (OrderItem oldItem : existingOrder.getOrderItems()) {
        List<MenuItemComponent> oldComponents = menuItemComponentRepo
            .findByMenuItem_MenuItemId(oldItem.getMenuItemId());
        for (MenuItemComponent comp : oldComponents) {
          InventoryItem invItem = inventoryService.getInventoryItem(comp.getInventoryItemId());
          double restoreQty = comp.getQuantity() * oldItem.getQuantity();
          invItem.setStoredQuantity(invItem.getStoredQuantity() + restoreQty);
          inventoryService.updateInventoryItem(invItem.getInventoryItemId(), invItem);
        }
      }
    }

    // SECOND: Process the updated order items.
    if (updatedOrder.getOrderItems() != null) {
      for (OrderItem newItem : updatedOrder.getOrderItems()) {
        newItem.setOrder(existingOrder); // Link each new item to the existing order.
        List<MenuItemComponent> newComponents = menuItemComponentRepo
            .findByMenuItem_MenuItemId(newItem.getMenuItemId());
        if (newComponents.isEmpty()) {
          throw new RuntimeException("No components found for menu item id: " + newItem.getMenuItemId());
        }
        for (MenuItemComponent comp : newComponents) {
          double requiredQty = comp.getQuantity() * newItem.getQuantity();
          InventoryItem invItem = inventoryService.getInventoryItem(comp.getInventoryItemId());
          if (invItem.getStoredQuantity() < requiredQty) {
            throw new RuntimeException("Item '" + invItem.getItemName()
                + "' is out of stock (needed: " + requiredQty + ", available: "
                + invItem.getStoredQuantity() + ").");
          }
          invItem.setStoredQuantity(invItem.getStoredQuantity() - requiredQty);
          inventoryService.updateInventoryItem(invItem.getInventoryItemId(), invItem);
        }
      }
    }

    // THIRD: Update order-level fields if provided.
    if (updatedOrder.getCashierId() != null) {
      existingOrder.setCashierId(updatedOrder.getCashierId());
    }
    if (updatedOrder.getTotalAmount() != null) {
      existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
    }
    if (updatedOrder.getOrderDate() != null) {
      existingOrder.setOrderDate(updatedOrder.getOrderDate());
    }
    if (updatedOrder.getOrderTime() != null) {
      existingOrder.setOrderTime(updatedOrder.getOrderTime());
    }
    if (updatedOrder.getIsRefund() != null) {
      existingOrder.setIsRefund(updatedOrder.getIsRefund());
    }
    // Replace order items
    existingOrder.setOrderItems(updatedOrder.getOrderItems());

    return orderRepo.save(existingOrder);
  }

  /**
   * Delete an order by its ID and restore inventory for each order item.
   * 
   * @param orderId: The order ID to be deleted from the database.
   */
  public void deleteOrder(Integer orderId) {
    // Retrieve the order to be deleted.
    Order existingOrder = orderRepo.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));

    // Restore the inventory for each order item before deleting the order.
    if (existingOrder.getOrderItems() != null) {
      for (OrderItem oldItem : existingOrder.getOrderItems()) {
        List<MenuItemComponent> components = menuItemComponentRepo.findByMenuItem_MenuItemId(oldItem.getMenuItemId());
        for (MenuItemComponent comp : components) {
          InventoryItem invItem = inventoryService.getInventoryItem(comp.getInventoryItemId());
          double restoreQty = comp.getQuantity() * oldItem.getQuantity();
          invItem.setStoredQuantity(invItem.getStoredQuantity() + restoreQty);
          inventoryService.updateInventoryItem(invItem.getInventoryItemId(), invItem);
        }
      }
    }

    orderRepo.delete(existingOrder);
  }
}
