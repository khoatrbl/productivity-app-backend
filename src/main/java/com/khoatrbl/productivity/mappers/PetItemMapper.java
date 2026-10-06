package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.PetItemDto;
import com.khoatrbl.productivity.domains.entities.PetItems;

public class PetItemMapper {
    public static PetItemDto toDto(PetItems petItems) {
        return PetItemDto.builder()
                .id(petItems.getId())
                .pet(PetMapper.toDto(petItems.getPet()))
                .shopItem(ShopItemsMapper.toDto(petItems.getShopItem()))
                .purchasedAt(petItems.getPurchasedAt())
                .isEquipped(petItems.isEquipped())
                .build();
    }
}
