package com.khoatrbl.productivity.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetFeedResponse {
    private PetDto pet;
    private InventoryItemDto inventoryItem;
    private int expGained;
    private int levelsGained; // 0 = no level-up; >1 = jumped several levels
    private int coins;
    private PetFortuneDto fortune;                 // null when no level-up
    private InventoryItemDto fortuneInventoryItem; // updated BASIC row when the fortune is treats
}
