package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.PetItemDto;
import com.khoatrbl.productivity.domains.entities.PetItems;

public class PetItemMapper {
    public static PetItemDto toDto(PetItems petItems) {
        return PetItemDto.builder()
                .id(petItems.getId())
                .shopItem(ShopItemsMapper.toDto(petItems.getShopItem()))
                .purchasedAt(petItems.getPurchasedAt())
                .equipped(petItems.isEquipped())
                .build();
    }
}
