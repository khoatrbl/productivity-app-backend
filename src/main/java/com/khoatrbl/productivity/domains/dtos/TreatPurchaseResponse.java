package com.khoatrbl.productivity.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatPurchaseResponse {
    private InventoryItemDto inventoryItem;
    private int coins;
    private int coinsSpent;
}
