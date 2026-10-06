package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.TreatTier;
import com.khoatrbl.productivity.domains.dtos.PurchaseRequest;
import com.khoatrbl.productivity.domains.dtos.TreatPurchaseResponse;
import com.khoatrbl.productivity.domains.entities.InventoryItem;
import com.khoatrbl.productivity.domains.entities.Treat;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.InsufficientResourceException;
import com.khoatrbl.productivity.mappers.InventoryItemMapper;
import com.khoatrbl.productivity.repositories.InventoryItemRepository;
import com.khoatrbl.productivity.repositories.TreatRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.InventoryItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryItemServiceImpl implements InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;
    private final TreatRepository treatRepository;
    private final UserRepository userRepository;

    @Override
    public List<InventoryItem> getAllItemsOfUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found for id: " + userId);
        }

        return inventoryItemRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public void initializeUserInventory(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found for id: " + userId)
                );

        List<InventoryItem> itemsToAdd = new ArrayList<>();

        for (TreatTier tier: TreatTier.values()) {
            Treat treat = treatRepository.findByTreatTier(tier);

            int quantity = 0;
            if (tier == TreatTier.BASIC) {
                quantity = 2;
            }

            InventoryItem item = InventoryItem.builder()
                    .user(user)
                    .treat(treat)
                    .quantity(quantity)
                    .build();

            itemsToAdd.add(item);
        }

        inventoryItemRepository.saveAll(itemsToAdd);
    }

    @Override
    @Transactional
    public TreatPurchaseResponse purchaseTreat(UUID userId, UUID treatId, PurchaseRequest purchaseRequest) {
        int quantity = purchaseRequest.getQuantity();

        Users user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        Treat treatToBuy = treatRepository.findById(treatId)
                .orElseThrow(() -> new EntityNotFoundException("Treat not found for id: " + treatId));

        int totalCost = Math.multiplyExact(treatToBuy.getPrice(), quantity);
        if (user.getCoins() < totalCost) {
            throw new InsufficientResourceException("Insufficient user resource.");
        }

        InventoryItem item = inventoryItemRepository.findByUserIdAndTreatIdForUpdate(userId, treatId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inventory row missing for user " + userId + " and treat " + treatId));

        user.setCoins(user.getCoins() - totalCost);
        item.setQuantity(item.getQuantity() + quantity);

        return TreatPurchaseResponse.builder()
                .inventoryItem(InventoryItemMapper.toDto(item))
                .coins(user.getCoins())
                .coinsSpent(totalCost)
                .build();
    }
}
