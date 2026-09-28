package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.CreatePetRequest;
import com.khoatrbl.productivity.domains.dtos.UpdatePetNameRequest;
import com.khoatrbl.productivity.domains.entities.PetLevels;
import com.khoatrbl.productivity.domains.entities.Pets;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.repositories.PetLevelRepository;
import com.khoatrbl.productivity.repositories.PetRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.PetService;
import com.khoatrbl.productivity.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {
    private final PetRepository petsRepository;
    private final PetLevelRepository petLevelRepository;
    private final UserService userService;
    private final UserRepository userRepository;


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
}
