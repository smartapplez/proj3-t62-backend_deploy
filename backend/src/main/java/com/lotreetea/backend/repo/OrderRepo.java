package com.lotreetea.backend.repo;

import com.lotreetea.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    // Additional query methods can be defined here if needed.
}
