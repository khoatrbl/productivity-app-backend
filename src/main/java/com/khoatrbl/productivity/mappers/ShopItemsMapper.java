package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.ShopItemsDto;
import com.khoatrbl.productivity.domains.entities.ShopItems;

public class ShopItemsMapper {
    public static ShopItemsDto toDto (ShopItems shopItem) {
        return ShopItemsDto.builder()
                .id(shopItem.getId())
                .requiredUserLevel(shopItem.getRequiredUserLevel().getLevel())
                .name(shopItem.getName())
                .description(shopItem.getDescription())
                .itemType(shopItem.getItemType())
                .price(shopItem.getPrice())
                .build();
    }
}
