package com.lotreetea.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuItemId;

    private String itemName;
    private String category;
    private BigDecimal price;
    // Add more fields if they exist in your DB table

    // NEW: One-to-many relationship to components (mapped by menuItemId in MenuItemComponent)
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<MenuItemComponent> components = new ArrayList<>();
}
