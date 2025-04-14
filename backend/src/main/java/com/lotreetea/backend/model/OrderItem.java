package com.lotreetea.backend.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    private Integer sugarPercentage;
    private Integer icePercentage;
    private Integer quantity;
    private Integer menuItemId; // Reference to the MenuItem (foreign key)

    // Extra fields for boba, popper, and jelly options
    private Boolean isBoba;
    private Boolean isPopper;
    private Boolean isJelly;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;
}
