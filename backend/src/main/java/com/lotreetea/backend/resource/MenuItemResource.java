package com.lotreetea.backend.resource;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

import com.lotreetea.backend.model.MenuItem;
import com.lotreetea.backend.service.MenuItemService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "https://csce331-project3-deploy-frontend.onrender.com") // or your front-end URL
@RestController
@RequestMapping("/menu_items")
@RequiredArgsConstructor
public class MenuItemResource {
    private final MenuItemService menuItemService;

    // CREATE
    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem saved = menuItemService.createMenuItem(menuItem);
        return ResponseEntity.created(URI.create("/menu_items/" + saved.getMenuItemId())).body(saved);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> items = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(items);
    }

    //READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Integer id) {
        MenuItem item = menuItemService.getMenuItem(id);
        return ResponseEntity.ok(item);
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Integer id,
            @RequestBody MenuItem updatedItem) {
        MenuItem updated = menuItemService.updateMenuItem(id, updatedItem);
        return ResponseEntity.ok(updated);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
