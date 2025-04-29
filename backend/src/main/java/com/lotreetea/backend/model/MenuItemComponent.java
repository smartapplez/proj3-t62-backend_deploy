package com.lotreetea.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_item_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Represents a component of a menu item in the restaurant management system.
 * Each component is associated with a specific menu item and has a quantity and cost.
 */
public class MenuItemComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Matching the DB schema

    // REMOVE this field to avoid duplicate mapping
    // private Integer menuItemId; // Foreign key to MenuItem table
    private Integer inventoryItemId; // Foreign key to InventoryItem table
    private double quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    @JsonBackReference
    private MenuItem menuItem;
}