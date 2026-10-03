package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.InventoryItemDto;
import com.khoatrbl.productivity.domains.entities.InventoryItem;

public class InventoryItemMapper {
    public static InventoryItemDto toDto(InventoryItem inventoryItem) {
        return InventoryItemDto.builder()
                .userId(inventoryItem.getUser().getId())
                .treatDto(TreatMapper.toDto(inventoryItem.getTreat()))
                .quantity(inventoryItem.getQuantity())
                .build();
    }
}
