package com.lotreetea.backend.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lotreetea.backend.model.MenuItemComponent;

public interface MenuItemComponentRepo extends JpaRepository<MenuItemComponent, Integer> {
    List<MenuItemComponent> findByMenuItem_MenuItemId(Integer menuItemId);
    void deleteByMenuItem_MenuItemId(Integer menuItemId);
    long countByInventoryItemId(Integer inventoryItemId); // Optional if you need orphan detection
}
