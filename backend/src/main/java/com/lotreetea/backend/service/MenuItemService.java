package com.lotreetea.backend.service;

import com.lotreetea.backend.dto.MenuItemRequest;
import com.lotreetea.backend.dto.MenuItemComponentRequest;
import com.lotreetea.backend.model.MenuItem;
import com.lotreetea.backend.model.MenuItemComponent;
import com.lotreetea.backend.model.InventoryItem;
import com.lotreetea.backend.repo.MenuItemRepo;
import com.lotreetea.backend.repo.MenuItemComponentRepo;
import com.lotreetea.backend.repo.InventoryItemRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class) 
public class MenuItemService {

    private final MenuItemRepo menuItemRepo;
    private final MenuItemComponentRepo menuItemComponentRepo;
    private final InventoryItemRepo inventoryItemRepo;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepo.findAllMenuItems();
    }

    public MenuItem getMenuItem(Integer id) {
        return menuItemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
    }

    public MenuItem createMenuItem(MenuItemRequest request) {
        MenuItem menuItem = new MenuItem();
        menuItem.setItemName(request.getItemName());
        menuItem.setCategory(request.getCategory());
        menuItem.setPrice(request.getPrice());
        menuItem = menuItemRepo.save(menuItem); 

        if (request.getComponents() != null) {
            for (MenuItemComponentRequest comp : request.getComponents()) {
                InventoryItem inventory = inventoryItemRepo.findById(comp.getInventoryItemId())
                        .orElseThrow(() -> new RuntimeException("Inventory item not found: " + comp.getInventoryItemId()));

                MenuItemComponent mic = new MenuItemComponent();
                mic.setInventoryItemId(comp.getInventoryItemId());
                mic.setQuantity(comp.getQuantity());

                mic.setMenuItem(menuItem);

                menuItemComponentRepo.save(mic);
                menuItem.getComponents().add(mic); 
            }
        }
        else{
            System.out.println("No components provided for menu item: " + menuItem.getItemName());
            new RuntimeException("null components for menu item: " + menuItem.getItemName());
        }
        return menuItem;
    }

public MenuItem updateMenuItem(Integer id, MenuItemRequest request) {
    MenuItem menuItem = menuItemRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));

    menuItem.setItemName(request.getItemName());
    menuItem.setCategory(request.getCategory());
    menuItem.setPrice(request.getPrice());
    menuItem = menuItemRepo.save(menuItem);

    menuItemComponentRepo.deleteByMenuItem_MenuItemId(id);

    if (request.getComponents() != null) {
        for (MenuItemComponentRequest comp : request.getComponents()) {
            InventoryItem inventory = inventoryItemRepo.findById(comp.getInventoryItemId())
                    .orElseThrow(() -> new RuntimeException("Inventory item not found: " + comp.getInventoryItemId()));

            MenuItemComponent mic = new MenuItemComponent();
            mic.setInventoryItemId(comp.getInventoryItemId());
            mic.setQuantity(comp.getQuantity());
            mic.setMenuItem(menuItem); 
            menuItemComponentRepo.save(mic);
        }
    }
    else{
        System.out.println("No components provided for menu item: " + menuItem.getItemName());
        new RuntimeException("null components for menu item: " + menuItem.getItemName());
    }

    return menuItem;
}
    public void deleteMenuItem(Integer id) {
        menuItemComponentRepo.deleteByMenuItem_MenuItemId(id);
        menuItemRepo.deleteById(id);
    }
}