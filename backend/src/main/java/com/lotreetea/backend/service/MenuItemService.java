package com.lotreetea.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.lotreetea.backend.model.MenuItem;
import com.lotreetea.backend.repo.MenuItemRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuItemService {
    private final MenuItemRepo menuItemRepo;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepo.findAllMenuItems();
    }

    public MenuItem getMenuItem(Integer id) {
        return menuItemRepo.findById(id).orElseThrow(() -> new RuntimeException("Menu Item not found"));
    }

    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepo.save(menuItem);
    }

    public MenuItem updateMenuItem(Integer id, MenuItem updatedItem) {
        MenuItem existing = getMenuItem(id);
        existing.setMenuItemId(updatedItem.getMenuItemId());
        existing.setItemName(updatedItem.getItemName());
        existing.setPrice(updatedItem.getPrice());
        existing.setCategory(updatedItem.getCategory());
        return menuItemRepo.save(existing);
    }

    public void deleteMenuItem(Integer id) {
        MenuItem item = getMenuItem(id); // throws if not found
        menuItemRepo.delete(item);
    }
}
