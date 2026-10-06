package com.khoatrbl.productivity.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetItemDto {
    private UUID id;

    private PetDto pet;

    private ShopItemsDto shopItem;

    private LocalDateTime purchasedAt;

    private boolean isEquipped;
}
