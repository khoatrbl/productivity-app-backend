package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.CreatePetRequest;
import com.khoatrbl.productivity.domains.dtos.PetDto;
import com.khoatrbl.productivity.domains.entities.Pets;
import com.khoatrbl.productivity.mappers.PetMapper;
import com.khoatrbl.productivity.services.PetService;
import com.khoatrbl.productivity.utilities.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetDto> createPetForUser(
            @Valid @RequestBody CreatePetRequest createPetRequest,
            Authentication authentication) {

        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        Pets pet = petService.createPetForUser(currentUserId, createPetRequest);

        PetDto dto = PetMapper.toDto(pet);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PetDto> getUsersPet(Authentication authentication) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        Pets pet = petService.getPetForUser(currentUserId);

        PetDto dto = PetMapper.toDto(pet);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}