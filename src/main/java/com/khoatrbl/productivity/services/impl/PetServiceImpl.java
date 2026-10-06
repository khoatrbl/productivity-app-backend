package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.CreatePetRequest;
import com.khoatrbl.productivity.domains.dtos.PetFeedRequest;
import com.khoatrbl.productivity.domains.dtos.PetFeedResponse;
import com.khoatrbl.productivity.domains.dtos.UpdatePetNameRequest;
import com.khoatrbl.productivity.domains.entities.*;
import com.khoatrbl.productivity.exceptions.InsufficientResourceException;
import com.khoatrbl.productivity.exceptions.MaxLevelReachedException;
import com.khoatrbl.productivity.mappers.InventoryItemMapper;
import com.khoatrbl.productivity.mappers.PetMapper;
import com.khoatrbl.productivity.repositories.*;
import com.khoatrbl.productivity.services.PetService;
import com.khoatrbl.productivity.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {
    private final PetRepository petsRepository;
    private final PetLevelRepository petLevelRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TreatRepository treatRepository;
    private final InventoryItemRepository inventoryItemRepository;


    @Override
    public Pets createPetForUser(UUID userId, CreatePetRequest createPetRequest) {
        Users currentUser = userService.getUserById(userId);
        PetLevels initialLevel = petLevelRepository.findByLevel(1)
                .orElseThrow(
                        () -> new EntityNotFoundException("Pet level not found for level: " + 1)
                );

        Pets pet = Pets.builder()
                .name(createPetRequest.getName())
                .owner(currentUser)
                .petLevel(initialLevel)
                .petCurrentExp(0)
                .currentAffectionPoint(0)
                .items(new ArrayList<>())
                .build();

        return petsRepository.save(pet);
    }

    @Override
    public Pets getPetForUser(UUID userId) {
        return petsRepository.findByOwnerId(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Pet not found for user: " + userId)
                );
    }

    @Override
    public Pets updatePetNameForUser(UUID userId, UpdatePetNameRequest updatePetNameRequest) {
        Pets pet = petsRepository.findByOwnerId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found for user: " + userId));

        pet.setName(updatePetNameRequest.getName());

        return petsRepository.save(pet);
    }

    @Override
    @Transactional
    public PetFeedResponse feedPetForUser(UUID userId, PetFeedRequest request) {
        UUID treatId = request.getTreatId();

        // Lock order: user -> inventory -> pet (same order as purchaseTreat)
        userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        InventoryItem inventoryItem = inventoryItemRepository.findByUserIdAndTreatIdForUpdate(userId, treatId)
                .orElseThrow(() -> new EntityNotFoundException("Treat not found in inventory: " + treatId));

        if (inventoryItem.getQuantity() <= 0) {
            throw new InsufficientResourceException("No treats of this kind left.");
        }

        Pets pet = petsRepository.findByOwnerIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found for user: " + userId));

        if (isAtMaxLevel(pet)) {
            // Refuse BEFORE consuming the treat, so it isn't wasted
            throw new MaxLevelReachedException("Your pet is already at max level.");
        }

        int gainedExp = inventoryItem.getTreat().getExp();

        inventoryItem.setQuantity(inventoryItem.getQuantity() - 1);
        int levelsGained = applyExp(pet, gainedExp);

        return PetFeedResponse.builder()
                .pet(PetMapper.toDto(pet))
                .inventoryItem(InventoryItemMapper.toDto(inventoryItem))
                .expGained(gainedExp)
                .levelsGained(levelsGained)
                .build();
    }

    /** Adds XP and levels up as many times as needed. Returns how many levels were gained. */
    private int applyExp(Pets pet, int gainedExp) {
        PetLevels level = pet.getPetLevel();
        int exp = pet.getPetCurrentExp() + gainedExp;
        int levelsGained = 0;

        while (exp >= level.getThreshold()) {
            Optional<PetLevels> next = petLevelRepository.findByLevel(level.getLevel() + 1);
            if (next.isEmpty()) {
                exp = level.getThreshold(); // reached max level: cap the bar at full
                break;
            }
            exp -= level.getThreshold();
            level = next.get();
            levelsGained++;
        }

        pet.setPetLevel(level);
        pet.setPetCurrentExp(exp);
        return levelsGained;
    }

    private boolean isAtMaxLevel(Pets pet) {
        PetLevels level = pet.getPetLevel();
        return pet.getPetCurrentExp() >= level.getThreshold()
                && petLevelRepository.findByLevel(level.getLevel() + 1).isEmpty();
    }
}
