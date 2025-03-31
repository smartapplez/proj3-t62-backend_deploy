package com.lotreetea.backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cashiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cashier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cashierId;

    private String firstName;
    private String lastName;
    private int pin;
    private boolean isManager;
}