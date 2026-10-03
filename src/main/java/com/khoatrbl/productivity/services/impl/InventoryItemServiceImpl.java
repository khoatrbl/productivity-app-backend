package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.TreatTier;
import com.khoatrbl.productivity.domains.entities.InventoryItem;
import com.khoatrbl.productivity.domains.entities.Treat;
import com.khoatrbl.productivity.domains.entities.Users;
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
}
