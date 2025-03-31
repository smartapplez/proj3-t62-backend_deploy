package com.lotreetea.backend.resource;

import com.lotreetea.backend.model.InventoryItem;
import com.lotreetea.backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "https://csce331-project3-deploy-frontend.onrender.com/")
// Allows cross-origin requests from front-end running on localhost:3000
@RestController // Indicates this class handles REST endpoints.
@RequestMapping("/inventory") // All endpoints will be prefixed with "/inventory".
@RequiredArgsConstructor
public class InventoryResource {

    private final InventoryService inventoryService;

    // CREATE
    @PostMapping
    public ResponseEntity<InventoryItem> createInventory(@RequestBody InventoryItem inventory) {
        InventoryItem saved = inventoryService.createInventoryItem(inventory);
        
        return ResponseEntity.created(URI.create("/inventory/" + saved.getInventoryItemId())).body(saved);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventories()
        {
        List<InventoryItem> all = inventoryService.getAllInventoryItems();
        return ResponseEntity.ok(all);
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventory(@PathVariable Integer id) {
        InventoryItem item = inventoryService.getInventoryItem(id);
        return ResponseEntity.ok(item);
    }

    

   
}
