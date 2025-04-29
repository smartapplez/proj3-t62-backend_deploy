package com.lotreetea.backend.resource;

import com.lotreetea.backend.model.MenuItem;
import com.lotreetea.backend.dto.MenuItemRequest;
import com.lotreetea.backend.service.MenuItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/menu_items")
@RequiredArgsConstructor

public class MenuItemResource {

    private final MenuItemService menuItemService;

    //GET ALL
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    //GET BY ID 
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MenuItem> getMenuById(@PathVariable Integer id) {
        MenuItem menuItem = menuItemService.getMenuItem(id);
        return ResponseEntity.ok(menuItem);
    }

    // POST (CREATE)
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItemRequest request) {
        MenuItem created = menuItemService.createMenuItem(request);
        // return ResponseEntity.created(URI.create("/menu_items/" + created.getMenuItemId())).body(created);
        return ResponseEntity.ok(created);
    }

    // PUT (UPDATE)
    // Replaces the entire resource
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Integer id, @RequestBody MenuItemRequest request) {
        MenuItem updated = menuItemService.updateMenuItem(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE (DELETE)
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}

/* JSON String Documentation:
 * GET Response Format (One Menu Item):
    {
    "menuItemId": 31,
    "itemName": "Matcha Milk Tea",
    "category": "Milk",
    "price": 5.50,
    "components": [
        {
            "id": 127,
            "inventoryItemId": 13,
            "quantity": 2.0
        },
        {
            "id": 128,
            "inventoryItemId": 19,
            "quantity": 1.0
        },
        {
            "id": 129,
            "inventoryItemId": 1,
            "quantity": 1.0
        }
        ]
    }

 * POST/PUT Body/Reponse Format (One Menu Item):
    {
    "itemName": "Matcha Milk Tea",
    "category": "Milk",
    "price": 5.50,
    "components": [
        {
        "inventoryItemId": 13,  // e.g. Black_Tea
        "quantity": 2
        },
        {
        "inventoryItemId": 19,  // e.g. Milk
        "quantity": 1
        },
        {
        "inventoryItemId": 1,   // e.g. Cup
        "quantity": 1
        }
    ]
    }

 *  This REQUIRES menuItemComponents to be included in JSON string
 *  Additionally, the inventoryItemId must exist in the database
 *  
 * GetMapping (Menu Item by ID):
 * GET http://localhost:8081/menu_items/{menu_item_id}
 * 
 * GetMapping (All Menu Items):
 * GET http://localhost:8081/menu_items
 * 
 * PostMapping (Create Menu Item):
 * POST http://localhost:8081/menu_items (with body shown above)
 * 
 * PutMapping (Update Menu Item):
 * PUT http://localhost:8081/menu_items/{menu_item_id} (with body shown above)
 * 
 * DeleteMapping (Delete Menu Item):
 * DELETE http://localhost:8081/menu_items/{menu_item_id}
 * 
 * If you want to request for a search by category, you can make an issue on that.
 * 
 * If you want to request for a search by item name, you can make an issue on that as well.
 */