package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.ItemType;
import com.khoatrbl.productivity.domains.dtos.PetItemPurchaseResponse;
import com.khoatrbl.productivity.domains.dtos.UpdatePetItemStateRequest;
import com.khoatrbl.productivity.domains.entities.PetItems;
import com.khoatrbl.productivity.domains.entities.Pets;
import com.khoatrbl.productivity.domains.entities.ShopItems;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.InsufficientResourceException;
import com.khoatrbl.productivity.exceptions.LevelRequirementNotMetException;
import com.khoatrbl.productivity.exceptions.PetItemAlreadyExistsForPetException;
import com.khoatrbl.productivity.mappers.PetItemMapper;
import com.khoatrbl.productivity.repositories.PetItemsRepository;
import com.khoatrbl.productivity.repositories.PetRepository;
import com.khoatrbl.productivity.repositories.ShopItemsRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.PetItemsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetItemsServiceImpl implements PetItemsService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PetItemsRepository petItemsRepository;
    private final ShopItemsRepository shopItemsRepository;

    @Override
    public List<PetItems> getAllPetItemsOfPetForUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found for id: " + userId);
        }

        Pets pet = petRepository.findByOwnerId(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Pet not found for user: " + userId)
                );

        return petItemsRepository.findAllByPetId(pet.getId());
    }

    @Override
    @Transactional
    public PetItemPurchaseResponse purchasePetItem(UUID userId, UUID shopItemId) {
        Users user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found for id: " + userId)
                );

        Pets pet = petRepository.findByOwnerIdForUpdate(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Pet not found for user: " + userId)
                );

        if (itemExistsForPet(pet, shopItemId)) {
            throw new PetItemAlreadyExistsForPetException("Pet item already exists for pet id: " + pet.getId());
        }

        ShopItems shopItem = shopItemsRepository.findById(shopItemId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Shop item not found for id: " + shopItemId)
                );

        if (user.getCurrentLevel().getLevel() < shopItem.getRequiredUserLevel().getLevel()) {
            throw new LevelRequirementNotMetException("Requires level " + shopItem.getRequiredUserLevel());
        }

        if (user.getCoins() < shopItem.getPrice()) {
            throw new InsufficientResourceException("Insufficient user resource.");
        }

        user.setCoins(user.getCoins() - shopItem.getPrice());

        PetItems petItem = PetItems.builder()
                .pet(pet)
                .shopItem(shopItem)
                .isEquipped(false)
                .build();

        petItemsRepository.save(petItem);
        pet.getItems().add(petItem);

        return PetItemPurchaseResponse.builder()
                .petItem(PetItemMapper.toDto(petItem))
                .coins(user.getCoins())
                .coinsSpent(shopItem.getPrice())
                .build();

    }

    @Override
    @Transactional
    public PetItems setPetItemStateForUsersPet(UUID userId, UUID petItemId, UpdatePetItemStateRequest request) {
        Pets pet = petRepository.findByOwnerIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found for user: " + userId));

        PetItems itemToUpdate = petItemsRepository.findById(petItemId)
                .filter(item -> item.getPet().getId().equals(pet.getId()))
                .orElseThrow(
                        () -> new EntityNotFoundException("Pet item not found for id: " + petItemId)
                );

        boolean equip = request.isEquipped();

        // Server-side rule: only one equipped item per itemType
        if (equip) {
            ItemType type = itemToUpdate.getShopItem().getItemType();

            pet.getItems().stream()
                    .filter(item -> item.isEquipped()
                            && !item.getId().equals(petItemId)
                            && item.getShopItem().getItemType() == type)
                    .forEach(item -> item.setEquipped(false));
        }

        itemToUpdate.setEquipped(equip);
        return itemToUpdate; // managed entity; flushed on commit
    }

    private boolean itemExistsForPet(Pets pet, UUID shopItemId) {
        return petItemsRepository.existsByPetIdAndShopItemId(pet.getId(), shopItemId);
    }
}
