package com.lotreetea.backend.resource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lotreetea.backend.model.Cashier;
import com.lotreetea.backend.service.CashierService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/cashiers")
@RequiredArgsConstructor
public class CashierResource {
    private final CashierService cashierService;

    @PostMapping
    public ResponseEntity<Cashier> createCashier(@RequestBody Cashier cashier) {
        Cashier saved = cashierService.createCashier(cashier);
        return ResponseEntity.created(URI.create("/cashiers/" + saved.getCashierId())).body(saved);
    }

    @GetMapping
    public ResponseEntity<Page<Cashier>> getCashiers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(cashierService.getAllCashiers(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cashier> getCashier(@PathVariable Integer id) {
        return ResponseEntity.ok().body(cashierService.getCashier(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Cashier>> getCashiersByLastName(@RequestParam String lastName) {
        List<Cashier> result = cashierService.getCashierByLastName(lastName);
        return ResponseEntity.ok(result);
    }

   
}
