package com.lotreetea.backend.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class MenuItemRequest {
    private String itemName;
    private String category;
    private BigDecimal price;
    private List<MenuItemComponentRequest> components;
}
