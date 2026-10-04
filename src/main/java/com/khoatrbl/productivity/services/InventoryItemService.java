package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.PurchaseRequest;
import com.khoatrbl.productivity.domains.entities.InventoryItem;

import java.util.List;
import java.util.UUID;

public interface InventoryItemService {
    List<InventoryItem> getAllItemsOfUserId(UUID userId);

    void initializeUserInventory(UUID userId);

    InventoryItem purchaseTreat(UUID userId, UUID treatId, PurchaseRequest purchaseRequest);
}
