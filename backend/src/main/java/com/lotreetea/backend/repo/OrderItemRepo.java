package com.lotreetea.backend.repo;

import com.lotreetea.backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    // Add query methods if needed
}
