package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.CreateShopItemRequest;
import com.khoatrbl.productivity.domains.entities.ShopItems;

import java.util.List;
import java.util.UUID;

public interface ShopItemsService {
    List<ShopItems> getAllShopItems();

    ShopItems getShopItems(UUID shopItemId);

    ShopItems createShopItem(CreateShopItemRequest createShopItemRequest);
}
