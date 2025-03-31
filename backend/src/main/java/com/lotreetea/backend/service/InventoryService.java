package com.lotreetea.backend.service;

import com.lotreetea.backend.model.InventoryItem;
import com.lotreetea.backend.repo.InventoryItemRepo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j //lombok auto logger
@Transactional(rollbackOn = Exception.class) // if exception undo 
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryItemRepo inventoryRepo;

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryRepo.findAll(Sort.by("itemName"));
    }

    public InventoryItem getInventoryItem(Integer id){
        return inventoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Inventory Item not found"));
    }

    public InventoryItem createInventoryItem(InventoryItem inventoryItem){
        return inventoryRepo.save(inventoryItem);
    }

    public void deleteInventoryItem(InventoryItem inventoryItem) {

    }
}
