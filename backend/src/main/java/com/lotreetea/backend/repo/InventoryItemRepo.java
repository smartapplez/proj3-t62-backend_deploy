package com.lotreetea.backend.repo;

import com.lotreetea.backend.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface  InventoryItemRepo extends JpaRepository<InventoryItem, Integer> {
    Optional<InventoryItem> findById(Integer id);
}
