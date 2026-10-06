package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.CreateShopItemRequest;
import com.khoatrbl.productivity.domains.entities.Level;
import com.khoatrbl.productivity.domains.entities.ShopItems;
import com.khoatrbl.productivity.repositories.LevelRepository;
import com.khoatrbl.productivity.repositories.ShopItemsRepository;
import com.khoatrbl.productivity.services.ShopItemsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopItemsServiceImpl implements ShopItemsService {
    private final ShopItemsRepository shopItemsRepository;
    private final LevelRepository levelRepository;

    @Override
    public List<ShopItems> getAllShopItems() {
        return shopItemsRepository.findAll();
    }

    @Override
    public ShopItems getShopItems(UUID shopItemId) {
        return shopItemsRepository.findById(shopItemId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Shop item not found for id: " + shopItemId)
                );
    }

    @Override
    public ShopItems createShopItem(CreateShopItemRequest createShopItemRequest) {
        Level requiredLevel =  levelRepository.findByLevel(createShopItemRequest.getRequiredUserLevel())
                .orElseThrow(
                        () -> new EntityNotFoundException("Level not found for level: " + createShopItemRequest.getRequiredUserLevel())
                );

        ShopItems item = ShopItems.builder()
                .name(createShopItemRequest.getName())
                .description(createShopItemRequest.getDescription())
                .itemType(createShopItemRequest.getItemType())
                .price(createShopItemRequest.getPrice())
                .requiredUserLevel(requiredLevel)
                .build();

        return shopItemsRepository.save(item);
    }
}
