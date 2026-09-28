package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItemsDto {
    private UUID id;

    private int requiredUserLevel;

    private String name;

    private String description;

    private ItemType itemType;

    private int price;
}
