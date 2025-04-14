package com.lotreetea.backend.repo;

import com.lotreetea.backend.model.Cashier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface CashierRepo extends JpaRepository<Cashier, Integer> {
    Optional<Cashier> findById(Integer id);
    List<Cashier> findByLastName(String lastName);
}