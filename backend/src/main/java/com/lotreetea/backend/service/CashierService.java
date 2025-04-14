package com.lotreetea.backend.service;

import com.lotreetea.backend.model.Cashier;
import com.lotreetea.backend.repo.CashierRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j //logging
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class CashierService {

    private final CashierRepo cashierRepo; //DAO

    public Page<Cashier> getAllCashiers(int page, int size) {
        return cashierRepo.findAll(PageRequest.of(page, size, Sort.by("lastName")));
        
    }

    public Cashier getCashier(Integer id) {
        return cashierRepo.findById(id).orElseThrow(() -> new RuntimeException("Cashier not found"));
    }

    public List<Cashier> getCashierByLastName(String lastName) {
        return cashierRepo.findByLastName(lastName);
    }

    public Cashier createCashier(Cashier cashier) {
        return cashierRepo.save(cashier);
    }

    public void deleteCashier(Cashier cashier) {
        cashierRepo.delete(cashier);
    }
}
