package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.CreatePetRequest;
import com.khoatrbl.productivity.domains.entities.Pets;

import java.util.UUID;

public interface PetService {
    Pets createPetForUser(UUID userId, CreatePetRequest createPetRequest);

    Pets getPetForUser(UUID userId);
}
