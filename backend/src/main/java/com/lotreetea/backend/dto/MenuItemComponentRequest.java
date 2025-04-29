package com.lotreetea.backend.dto;

import lombok.Data;

@Data
public class MenuItemComponentRequest {
    private Integer inventoryItemId;
    private Integer quantity;
}
