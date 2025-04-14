package com.lotreetea.backend.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lotreetea.backend.model.MenuItem;

import java.util.Optional;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Integer> {

    @Query("SELECT m FROM MenuItem m")
    List<MenuItem> findAllMenuItems();

    Optional<MenuItem> findById(Integer id); // Optional to handle cases where the item might not exist
}
