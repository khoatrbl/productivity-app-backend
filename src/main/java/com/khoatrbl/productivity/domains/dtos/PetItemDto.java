package com.khoatrbl.productivity.domains.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private ShopItemsDto shopItem;

    private LocalDateTime purchasedAt;

    @JsonProperty("isEquipped")
    private boolean isEquipped;
}
